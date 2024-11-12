package com.move.dto;

import lombok.Builder;

@Builder
public record ErrorResponse(int status, String message, String details) {
}
