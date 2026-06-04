package com.pickdo.backend.global.response;

public record ResponseEnvelope<T>(
        boolean success,
        T data,
        ErrorResponse error
) {

    public static <T> ResponseEnvelope<T> success(T data) {
        return new ResponseEnvelope<>(true, data, null);
    }

    public static <T> ResponseEnvelope<T> error(String code, String message, Object details) {
        return new ResponseEnvelope<>(false, null, new ErrorResponse(code, message, details));
    }

    public record ErrorResponse(
            String code,
            String message,
            Object details
    ) {
    }
}
