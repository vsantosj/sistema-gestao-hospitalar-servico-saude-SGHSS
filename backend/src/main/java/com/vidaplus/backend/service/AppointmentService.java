package com.vidaplus.backend.service;

import com.vidaplus.backend.dto.request.AppointmentCreateRequest;
import com.vidaplus.backend.dto.response.AppointmentResponse;
import com.vidaplus.backend.dto.request.AppointmentUpdateRequest;
import com.vidaplus.backend.mapper.AppointmentMapper;
import com.vidaplus.backend.model.Appointment;
import com.vidaplus.backend.model.StatusAppointment;
import com.vidaplus.backend.model.User;
import com.vidaplus.backend.repository.AppointmentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository){
        this.appointmentRepository = appointmentRepository;
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // ⭐ MODIFICADO: Agora lista apenas appointments do usuário logado
    public List<AppointmentResponse> appointmentList(){
        User user = getAuthenticatedUser();
        List<Appointment> appointments = appointmentRepository.findByUser(user);
        return appointments.stream()
                .map(AppointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse createAppointment(@Valid AppointmentCreateRequest req){
        User user = getAuthenticatedUser();

        validDuration(req.startTime(), req.endTime());
        checkConflit(req.patient(), req.startTime(), req.endTime(), null);

        Appointment entity = AppointmentMapper.toEntity(req);
        entity.setUser(user);  // ⭐ Associa ao usuário logado
        entity = appointmentRepository.save(entity);
        return AppointmentMapper.toResponse(entity);
    }

    @Transactional
    public AppointmentResponse updateAppointment(Long id, @Valid AppointmentUpdateRequest req){
        User user = getAuthenticatedUser();

        Appointment entity = appointmentRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado ou você não tem permissão"));

        AppointmentMapper.merge(entity, req);
        validDuration(req.startTime(), req.endTime());
        checkConflit(entity.getPatient(), req.startTime(), req.endTime(), entity.getId());

        entity = appointmentRepository.save(entity);
        return AppointmentMapper.toResponse(entity);
    }

    @Transactional
    public AppointmentResponse statusCancelAppointment(Long id){
        User user = getAuthenticatedUser();

        Appointment entity = appointmentRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado ou você não tem permissão"));

        entity.setStatus(StatusAppointment.CANCELADO);
        entity = appointmentRepository.save(entity);
        return AppointmentMapper.toResponse(entity);
    }

    @Transactional
    public AppointmentResponse statusOkAppointment(Long id){
        User user = getAuthenticatedUser();

        Appointment entity = appointmentRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado ou você não tem permissão"));

        entity.setStatus(StatusAppointment.CONCLUIDO);
        entity = appointmentRepository.save(entity);
        return AppointmentMapper.toResponse(entity);
    }

    public AppointmentResponse findById(Long id){
        User user = getAuthenticatedUser();

        Appointment appointment = appointmentRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado ou você não tem permissão"));

        return AppointmentMapper.toResponse(appointment);
    }

    private void validDuration(LocalTime start, LocalTime end){
        if(start == null || end == null || !start.isBefore(end)){
            throw new IllegalArgumentException("Intervalo inválido, horario inicio deve ser anterior ao horario final");
        }
    }

    private void checkConflit(String patient, LocalTime start, LocalTime end, Long id){
        if(appointmentRepository.existsConflit(patient, start, end, id)){
            throw new IllegalArgumentException("Conflito na agenda, já existe um agendamento nessa agenda");
        }
    }
}