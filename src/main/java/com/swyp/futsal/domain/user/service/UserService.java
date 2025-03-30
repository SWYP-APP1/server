package com.swyp.futsal.domain.user.service;

import com.swyp.futsal.domain.user.entity.User;
import com.swyp.futsal.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {
    return userRepository.findByUid(uid)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with uid: " + uid));
  }

  @Transactional
  public User register(String uid, String email, String nickname) {
    User customUser = User.builder()
        .uid(uid)
        .email(email)
        .name(nickname)
        .build();
    userRepository.save(customUser);
    return customUser;
  }
}
