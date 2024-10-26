package ru.moonlightapp.backend.storage.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.moonlightapp.backend.model.Sex;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Entity @Table(name = "users")
public final class User implements UserDetails {

    @Id
    @Column(name = "email", nullable = false, length = 64)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", length = 64)
    private String name;

    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    private LocalDate birthDate;

    @Column(name = "sex", length = 6)
    private Sex sex;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public User(String email, String password) {
        this(email, password, null, null, null);
    }

    public User(String email, String password, String name, LocalDate birthDate, Sex sex) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthDate = birthDate;
        this.sex = sex;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public void changeName(String name) {
        this.name = name;
        onDataUpdated();
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
        onDataUpdated();
    }

    public void changeBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        onDataUpdated();
    }

    public void changeSex(Sex sex) {
        this.sex = sex;
        onDataUpdated();
    }

    private void onDataUpdated() {
        this.updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + (password != null ? "<masked>" : "<not set>") + '\'' +
                ", name='" + name + '\'' +
                ", birthDate=" + birthDate +
                ", sex=" + sex +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
