package com.yunnan.jobmonitor.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record SurveyPeriodWriteDto(
    @NotBlank String name, @NotNull Instant reportStart, @NotNull Instant reportEnd, boolean active) {}
