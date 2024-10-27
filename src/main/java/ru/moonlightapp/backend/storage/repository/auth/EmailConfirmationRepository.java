package ru.moonlightapp.backend.storage.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.moonlightapp.backend.storage.model.auth.EmailConfirmation;

public interface EmailConfirmationRepository extends JpaRepository<EmailConfirmation, String> {

}