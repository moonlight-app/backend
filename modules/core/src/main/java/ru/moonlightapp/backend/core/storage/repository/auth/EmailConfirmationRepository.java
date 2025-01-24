package ru.moonlightapp.backend.core.storage.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.moonlightapp.backend.core.storage.model.auth.EmailConfirmation;

public interface EmailConfirmationRepository extends JpaRepository<EmailConfirmation, String> {

}