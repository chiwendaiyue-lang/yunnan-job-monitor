package com.yunnan.jobmonitor.web.dto;

import com.yunnan.jobmonitor.domain.RoleCode;

public record UserProfile(
    Long id, String username, String displayName, RoleCode role, Long enterpriseId, String regionCode) {}
