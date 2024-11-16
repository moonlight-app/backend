package ru.moonlightapp.backend.boot;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.moonlightapp.backend.boot.option.ImportDumpStartupOption;

@Component
@RequiredArgsConstructor
public final class MoonlightStartupRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (args.containsOption("import-dump")) {
            new ImportDumpStartupOption(jdbcTemplate).handleStartupOption(args.getOptionValues("import-dump"));
        }
    }

}
