package org.decepticons.linkshortener.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ApiErrorResponseDto {
  private LocalDateTime timestamp;
  private int status;
  private String error;
  private String message;
  private String path;     
  private Object details;
}
