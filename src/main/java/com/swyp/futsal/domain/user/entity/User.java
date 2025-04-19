package com.swyp.futsal.domain.user.entity;

import com.swyp.futsal.domain.common.BaseEntity;
import com.swyp.futsal.domain.common.enums.Gender;
import com.swyp.futsal.domain.common.enums.Platform;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.Optional;

@Entity
@Table(name = "user")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String uid;

    @Column(name = "profile_uri")
    private String profileUri;

    @Column
    private String name;

    @Column(name = "birth_date")
    private String birthDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Gender gender = Gender.NONE;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    @Column(name = "squad_number")
    private Integer squadNumber;

    @Column(name = "agreement")
    @Builder.Default
    private boolean agreement = false;

    @Column()
    @Builder.Default
    private boolean notification = false;

    @Column(name = "is_deleted")
    @Builder.Default
    private boolean isDeleted = false;

    public String getUid() {
        return uid;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
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
        return !isDeleted;
    }

    public Optional<String> calculateGeneration() {
        if (birthDate == null) {
            return Optional.empty();
        }

        LocalDate birthLocalDate = LocalDate.parse(birthDate);
        LocalDate now = LocalDate.now();
        
        int age = Period.between(birthLocalDate, now).getYears();
        if (age >= 20 && age < 30) {
            return Optional.of("20대");
        } else if (age >= 30 && age < 40) {
            return Optional.of("30대");
        } else if (age >= 40 && age < 50) {
            return Optional.of("40대");
        } else if (age >= 50 && age < 60) {
            return Optional.of("50대");
        } else if (age >= 60) {
            return Optional.of("60대 이상");
        } else {
            return Optional.of("10대 이하");
        }
    }
}