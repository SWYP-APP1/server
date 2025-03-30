package com.swyp.futsal.domain.user.service;

import com.swyp.futsal.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSecurityService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {
    return userRepository.findByUid(uid)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with uid: " + uid));
  }

}
