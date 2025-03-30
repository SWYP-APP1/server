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

import java.util.Collection;

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
}