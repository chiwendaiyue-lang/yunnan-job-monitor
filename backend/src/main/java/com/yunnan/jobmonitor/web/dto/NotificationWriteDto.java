package com.yunnan.jobmonitor.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NotificationWriteDto(
    @NotBlank @Size(max = 100) String title, @NotBlank @Size(max = 2000) String content) {}
