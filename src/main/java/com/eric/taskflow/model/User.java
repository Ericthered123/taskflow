package com.eric.taskflow.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;


@Entity
@Table(name = "users", indexes = {
        @Index(columnList = "username", name = "idx_users_username")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username; //username único + índice para búsqueda rápida.

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;


    @Column(nullable = false)
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false, updatable = false)
    private Instant createdAt; //Instant para timestamps(mejor con UTC).


    @PrePersist //@PrePersist asegura createdAt y rol por defecto.
    public void prePersist() {
        this.createdAt = Instant.now();
        if (this.role == null) this.role = Role.USER;
    }

    // ---------- MÉTODOS DE USERDETAILS ----------
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Cambiar si se quiere manejar expiración
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Cambiar si manejo de bloqueos
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Cambiar si expiran contraseñas
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
