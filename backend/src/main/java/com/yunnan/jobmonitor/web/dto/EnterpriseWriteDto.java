package com.yunnan.jobmonitor.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EnterpriseWriteDto(
    @NotBlank @Size(max = 32) String regionCity,
    @NotBlank @Size(max = 32) String regionCounty,
    @Size(max = 128) String regionArea,
    @NotBlank @Size(max = 9) @Pattern(regexp = "^[A-Za-z0-9]{1,9}$") String orgCode,
    @NotBlank @Size(max = 256) String name,
    @NotBlank @Size(max = 64) String natureLevel1,
    @NotBlank @Size(max = 64) String natureLevel2,
    @NotBlank @Size(max = 64) String industryLevel1,
    @NotBlank @Size(max = 64) String industryLevel2,
    @NotBlank @Size(max = 512) String mainBusiness,
    @NotBlank @Size(max = 128) String contactName,
    @NotBlank @Size(max = 64) String addressLevel1,
    @NotBlank @Size(max = 64) String addressLevel2,
    @NotBlank @Size(max = 256) String addressDetail,
    @NotBlank @Pattern(regexp = "^\\d{6}$") String postalCode,
    @NotBlank @Size(max = 64) String phone,
    @NotBlank @Size(max = 64) String fax,
    @Email @Size(max = 128) String email) {}
