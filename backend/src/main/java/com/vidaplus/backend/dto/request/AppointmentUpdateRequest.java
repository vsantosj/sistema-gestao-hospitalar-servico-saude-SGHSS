package com.vidaplus.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentUpdateRequest(
        @Size(max=4000) String description,
        @NotNull LocalDate date,
        LocalTime startTime,
        LocalTime endTime
) {
}
