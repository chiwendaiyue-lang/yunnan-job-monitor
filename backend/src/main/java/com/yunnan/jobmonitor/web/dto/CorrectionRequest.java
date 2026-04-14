package com.yunnan.jobmonitor.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CorrectionRequest(
    @NotBlank String fieldName, @NotBlank String newValue, @NotBlank String reason) {}
