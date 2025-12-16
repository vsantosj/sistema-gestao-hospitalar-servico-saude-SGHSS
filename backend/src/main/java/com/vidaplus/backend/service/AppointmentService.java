package com.vidaplus.backend.service;

import com.vidaplus.backend.dto.request.AppointmentCreateRequest;
import com.vidaplus.backend.dto.response.AppointmentResponse;
import com.vidaplus.backend.dto.request.AppointmentUpdateRequest;
import com.vidaplus.backend.mapper.AppointmentMapper;
import com.vidaplus.backend.model.Appointment;
import com.vidaplus.backend.model.StatusAppointment;
import com.vidaplus.backend.repository.AppointmentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

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

    public List<AppointmentResponse> appointmentList(){
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream()
                .map(AppointmentMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Transactional
    public AppointmentResponse createAppointment(@Valid AppointmentCreateRequest req){

        validDuration(req.startTime(),req.endTime());
        checkConflit(req.patient(), req.startTime(), req.endTime(), null);


        Appointment entity = AppointmentMapper.toEntity(req);
        entity = appointmentRepository.save(entity);
        return  AppointmentMapper.toResponse(entity);
    }

    @Transactional
    public AppointmentResponse updateAppointment(Long id, @Valid AppointmentUpdateRequest req){
        Appointment entity = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));
        AppointmentMapper.merge(entity, req);
        AppointmentMapper.merge(entity,req);
        validDuration(req.startTime(),req.endTime());
        checkConflit(entity.getPatient(), req.startTime(), req.endTime(), entity.getId());


        entity = appointmentRepository.save(entity);
        return AppointmentMapper.toResponse(entity);
    }

    @Transactional
    public AppointmentResponse statusCancelAppointment(Long id){
        Appointment entity = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));
        entity.setStatus(StatusAppointment.CANCELADO);
        entity = appointmentRepository.save(entity);
        return AppointmentMapper.toResponse(entity);
    }

    @Transactional
    public AppointmentResponse statusOkAppointment(Long id){
        Appointment entity = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));
        entity.setStatus(StatusAppointment.CONCLUIDO);
        entity = appointmentRepository.save(entity);
        return AppointmentMapper.toResponse(entity);

    }

    public AppointmentResponse findById(Long id){
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado"));

        return AppointmentMapper.toResponse(appointment);
    }

    private void validDuration(LocalTime start, LocalTime end){
        if(start == null || end == null || !start.isBefore(end)){
            throw new IllegalArgumentException(("Intervalo inválido, horario inicio deve ser anterior a ao horario final"));
        }

    }
    private void checkConflit(String patient, LocalTime start, LocalTime end, Long id){
        if(appointmentRepository.existsConflit(patient, start, end, id)){
            throw new IllegalArgumentException("Conflito na agenda, já existe um agendamento nessa agenda");
        }
    }
}
