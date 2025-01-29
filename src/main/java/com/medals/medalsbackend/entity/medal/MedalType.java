package com.medals.medalsbackend.entity.medal;

public enum MedalType {
    GOLD,
    SILVER,
    BRONZE;

    @Override
    public String toString() {
        return switch (this) {
            case GOLD -> "gold";
            case BRONZE -> "bronze";
            case SILVER -> "silver";
        };
    }
}
