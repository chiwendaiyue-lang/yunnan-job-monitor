package com.yunnan.jobmonitor.web.error;

public class BusinessException extends RuntimeException {
  public BusinessException(String message) {
    super(message);
  }
}
