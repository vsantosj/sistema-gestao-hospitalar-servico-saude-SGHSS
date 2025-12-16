package com.vidaplus.backend.repository;

import com.vidaplus.backend.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
            FROM Appointment a
            WHERE a.patient = :patient
                AND a.status = com.vidaplus.backend.model.StatusAppointment.AGENDADO
                AND (a.startTime < :end AND a.endTime > :start)
                AND (:ignoreId is NULL OR a.id <> : ignoreId)
""")

    boolean existsConflit(@Param("patient")String patient,
                          @Param("start")LocalTime start,
                          @Param("end") LocalTime end,
                          @Param("ignoreId") Long ignoreId);
}
