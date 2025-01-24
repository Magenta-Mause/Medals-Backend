package com.medals.medalsbackend.api;

public enum ApiStatus {
    SUCCESS,
    NOT_IMPLEMENTED,
    REFER_HTTP_STATUS,
    ERROR;

    public String toString() {
        switch (this) {
            case SUCCESS -> { return "success"; }
            case NOT_IMPLEMENTED -> { return "not implemented"; }
            case REFER_HTTP_STATUS -> { return "other"; }
            case ERROR -> { return "refer to http status code"; }
            default -> { return "unknown"; }
        }
    }
}
