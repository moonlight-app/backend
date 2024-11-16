package ru.moonlightapp.backend.boot.option;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.moonlightapp.backend.util.DumpImporter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public final class ImportDumpStartupOption implements StartupOptionHandler {

    private final JdbcTemplate jdbcTemplate;
    private final Path dumpZipPath;

    public ImportDumpStartupOption(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.dumpZipPath = Paths.get("dump.zip");
    }

    @Override
    public void handleStartupOption(List<String> values) {
        if (!Files.isRegularFile(dumpZipPath)) {
            log.error("The 'dump.zip' file doesn't exist.");
            return;
        }

        DumpImporter.importDump(dumpZipPath, jdbcTemplate);
    }

}
