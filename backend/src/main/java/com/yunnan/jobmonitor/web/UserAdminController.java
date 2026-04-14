package com.yunnan.jobmonitor.web;

import com.yunnan.jobmonitor.domain.SysUser;
import com.yunnan.jobmonitor.service.UserAdminService;
import com.yunnan.jobmonitor.web.dto.UserCreateDto;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/province/users")
public class UserAdminController {

  private final UserAdminService userAdminService;

  public UserAdminController(UserAdminService userAdminService) {
    this.userAdminService = userAdminService;
  }

  @GetMapping
  public List<SysUser> list() {
    return userAdminService.listAll();
  }

  @PostMapping
  public SysUser create(@Valid @RequestBody UserCreateDto dto) {
    return userAdminService.create(dto);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    userAdminService.delete(id);
  }
}
