package ru.moonlightapp.backend.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@UtilityClass
public class DumpImporter {

    public static void importDump(Path dumpZipPath, JdbcTemplate jdbcTemplate) {
        Map<DumpType, String> dumps = new TreeMap<>();

        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(dumpZipPath))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                if (entry.isDirectory() || !entryName.toLowerCase().endsWith(".sql"))
                    continue;

                Optional<DumpType> dumpType = DumpType.resolveByFileName(entryName);
                if (dumpType.isEmpty()) {
                    log.warn("Skipped SQL script '{}': no matched dump type found!", entryName);
                    continue;
                }

                byte[] allBytes = zipInputStream.readAllBytes();
                String sql = new String(allBytes, StandardCharsets.UTF_8);
                dumps.put(dumpType.get(), sql);
            }
        } catch (IOException ex) {
            log.error("Couldn't read dump file!", ex);
        }

        if (!dumps.isEmpty()) {
            dumps.forEach((dumpType, sql) -> {
                log.info("Importing dump '{}'...", dumpType);
                log.info("  Executing SQL script: {} character(s)...", sql.length());
                jdbcTemplate.execute(sql);
            });
        }
    }

    @Getter
    @AllArgsConstructor
    private enum DumpType {

        PRODUCTS   ("products",                 "Products"),
        SIZES      ("product_sizes",            "Product Sizes"),
        MAPPINGS   ("product_size_mappings",    "Product <-> Product Sizes"),
        ;

        private final String destName;
        private final String displayName;

        private static Optional<DumpType> resolveByFileName(String fileName) {
            fileName = fileName.toLowerCase();

            for (DumpType dumpType : values())
                if (fileName.startsWith(dumpType.getDestName()))
                    return Optional.of(dumpType);

            return Optional.empty();
        }

    }

}
