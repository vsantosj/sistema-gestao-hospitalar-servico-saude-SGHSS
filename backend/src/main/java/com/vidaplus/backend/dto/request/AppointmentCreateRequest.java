package com.vidaplus.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentCreateRequest(
        @NotBlank @Size(max = 120) String title,
        @Size(max=4000) String description,
        @NotNull LocalDate date,
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime,
        @NotBlank @Size(max = 100) String patient,
        @NotBlank @Size(max = 100) String doctor
) {
}
