package com.spring.guidely.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;


@Entity
@Table(name = "app_users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AppUser implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;


    @Column(nullable = false)
    private String password;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Department getDepartment() {
        return department;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = true)
    private Department department;


    public void setDepartment(Department department) {
        this.department = department;
    }
    // Constructors, getters, and setters...
    // =====================================



    @Override
    public String getUsername() {
        // By default, Spring uses 'username' to authenticate,
        // but we are using 'email' as the unique user identifier.
        return email;
    }



    // This method from UserDetails -> Return the password
    @Override
    public String getPassword() {
        return password;
    }



    // === Methods from UserDetails interface ===

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // We can return a collection of authorities based on the role's name
        // e.g., "ROLE_USER", "ROLE_ADMIN"
        return Collections.singleton(() -> "ROLE_" + role.getName());
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;  // or implement logic if you have an "expired" field
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;  // or implement logic if you have a "locked" field
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // or implement logic if you want to expire credentials
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;  // or implement logic if you have an "enabled" field
    }



}