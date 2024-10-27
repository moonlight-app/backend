package ru.moonlightapp.backend.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.moonlightapp.backend.storage.model.User;

public interface UserRepository extends JpaRepository<User, String> {

}
