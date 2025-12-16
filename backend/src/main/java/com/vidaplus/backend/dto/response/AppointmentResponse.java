package com.vidaplus.backend.dto.response;


import com.vidaplus.backend.model.StatusAppointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AppointmentResponse(
        Long id,
        String title,
        String description,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String patient,
        String doctor,
        StatusAppointment status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
