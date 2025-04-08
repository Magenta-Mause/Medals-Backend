package com.medals.medalsbackend.entity.swimCertificate;

public enum SwimCertificateType {
    ENDURANCE,  // Fully continuous swim (distance, time; bronze time not mandatory)
    SPRINT,     // 25m swim within bronze time (or better)
    JUNIOR,     // Under 12: 50m continuous without time limit
    SENIOR,     // 12 and over: 200m in max 11 minutes continuously
    SUSTAINED,  // 15-minute continuous swim (open water possible, clear movement required)
    CLOTHED,    // 100m clothed swim in max 4 minutes with in-water undressing
    BADGES      // Submission of recognized swim badges
}
