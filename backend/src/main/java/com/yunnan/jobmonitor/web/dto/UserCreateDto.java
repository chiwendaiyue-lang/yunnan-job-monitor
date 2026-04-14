package com.yunnan.jobmonitor.web.dto;

import com.yunnan.jobmonitor.domain.RoleCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserCreateDto(
    @NotBlank @Size(max = 64) String username,
    @NotBlank @Size(min = 8, max = 64) String password,
    @Size(max = 128) String displayName,
    @NotNull RoleCode roleCode,
    String regionCode,
    Long enterpriseId) {}
