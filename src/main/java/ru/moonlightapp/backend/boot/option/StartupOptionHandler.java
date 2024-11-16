package ru.moonlightapp.backend.boot.option;

import java.util.List;

public interface StartupOptionHandler {

    void handleStartupOption(List<String> values);

}
