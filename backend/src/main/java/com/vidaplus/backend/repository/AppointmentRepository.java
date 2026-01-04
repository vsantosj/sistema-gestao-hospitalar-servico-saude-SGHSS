package com.vidaplus.backend.repository;

import com.vidaplus.backend.model.Appointment;
import com.vidaplus.backend.model.StatusAppointment;
import com.vidaplus.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
            FROM Appointment a
            WHERE a.patient = :patient
                AND a.status = com.vidaplus.backend.model.StatusAppointment.AGENDADO
                AND (a.startTime < :end AND a.endTime > :start)
                AND (:ignoreId is NULL OR a.id <> :ignoreId)
    """)
    boolean existsConflit(@Param("patient")String patient,
                          @Param("start")LocalTime start,
                          @Param("end") LocalTime end,
                          @Param("ignoreId") Long ignoreId);

    // ⭐ NOVOS MÉTODOS - Adicione aqui
    List<Appointment> findByUser(User user);

    Optional<Appointment> findByIdAndUser(Long id, User user);

    List<Appointment> findByUserAndStatus(User user, StatusAppointment status);
}