package com.yunnan.jobmonitor.security;

import com.yunnan.jobmonitor.repo.SysUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

  private final SysUserRepository userRepository;

  public AppUserDetailsService(SysUserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var u =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));
    return User.builder()
        .username(u.getUsername())
        .password(u.getPasswordHash())
        .disabled(!u.isActive())
        .authorities("ROLE_" + u.getRole().getCode().name())
        .build();
  }
}
