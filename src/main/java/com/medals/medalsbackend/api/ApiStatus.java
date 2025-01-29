package com.medals.medalsbackend.api;

public enum ApiStatus {
    INFORMATION,
    SUCCESS,
    REDIRECT,
    CLIENT_ERROR,
    SERVER_ERROR;

    public static ApiStatus fromCode(int code) {
        if (code <= 199) {
            return INFORMATION;
        }
        if (code <= 299) {
            return SUCCESS;
        }
        if (code <= 399) {
            return REDIRECT;
        }
        if (code <= 499) {
            return CLIENT_ERROR;
        }
        return SERVER_ERROR;
    }

    public String toString() {
        return switch (this) {
            case INFORMATION -> "information";
            case SUCCESS -> "success";
            case REDIRECT -> "redirect";
            case CLIENT_ERROR -> "client_error";
            case SERVER_ERROR -> "server_error";
        };
    }
}
