package com.vidaplus.backend.mapper;

import com.vidaplus.backend.dto.request.AppointmentCreateRequest;
import com.vidaplus.backend.dto.response.AppointmentResponse;
import com.vidaplus.backend.dto.request.AppointmentUpdateRequest;
import com.vidaplus.backend.model.Appointment;
import com.vidaplus.backend.model.StatusAppointment;

import java.time.LocalDateTime;

public class AppointmentMapper {


    public static Appointment toEntity(AppointmentCreateRequest req){
        return Appointment.builder()
                .title(req.title())
                .description(req.description())
                .date(req.date())
                .startTime(req.startTime())
                .endTime(req.endTime())
                .patient(req.patient())
                .doctor(req.doctor())
                .status(StatusAppointment.AGENDADO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static void merge(Appointment entity, AppointmentUpdateRequest req){
        if(req.description() != null){
            entity.setDescription(req.description());
        }
        if(req.startTime() !=null){
            entity.setStartTime(req.startTime());
        }
        if(req.endTime() !=null){
            entity.setEndTime(req.endTime());
        }

    }


    public static AppointmentResponse toResponse(Appointment a){
        return new AppointmentResponse(
                        a.getId(),
                        a.getTitle(),
                        a.getDescription(),
                        a.getDate(),
                        a.getStartTime(),
                        a.getEndTime(),
                        a.getPatient(),
                        a.getDoctor(),
                        a.getStatus(),
                        a.getCreatedAt(),
                        a.getUpdatedAt());
    }

}