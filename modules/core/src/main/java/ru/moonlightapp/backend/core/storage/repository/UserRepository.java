package ru.moonlightapp.backend.core.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.moonlightapp.backend.core.storage.model.User;

public interface UserRepository extends JpaRepository<User, String> {

}
