package com.yunnan.jobmonitor.web;

import com.yunnan.jobmonitor.domain.SysUser;
import com.yunnan.jobmonitor.repo.SysUserRepository;
import com.yunnan.jobmonitor.security.JwtService;
import com.yunnan.jobmonitor.service.CurrentUserService;
import com.yunnan.jobmonitor.web.dto.LoginRequest;
import com.yunnan.jobmonitor.web.dto.LoginResponse;
import com.yunnan.jobmonitor.web.dto.UserProfile;
import com.yunnan.jobmonitor.web.error.BusinessException;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final SysUserRepository userRepository;
  private final JwtService jwtService;
  private final CurrentUserService currentUserService;

  public AuthController(
      AuthenticationManager authenticationManager,
      SysUserRepository userRepository,
      JwtService jwtService,
      CurrentUserService currentUserService) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.jwtService = jwtService;
    this.currentUserService = currentUserService;
  }

  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest req) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(req.username(), req.password()));
    SysUser u =
        userRepository
            .findByUsername(req.username())
            .filter(SysUser::isActive)
            .orElseThrow(() -> new BusinessException("用户无效"));
    return new LoginResponse(jwtService.generateToken(u.getUsername()), toProfile(u));
  }

  @GetMapping("/me")
  public UserProfile me() {
    return toProfile(currentUserService.requireUser());
  }

  private static UserProfile toProfile(SysUser u) {
    return new UserProfile(
        u.getId(),
        u.getUsername(),
        u.getDisplayName(),
        u.getRole().getCode(),
        u.getEnterpriseId(),
        u.getRegionCode());
  }
}
