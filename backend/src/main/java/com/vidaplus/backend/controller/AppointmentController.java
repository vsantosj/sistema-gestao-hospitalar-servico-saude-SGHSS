package com.vidaplus.backend.controller;

import com.vidaplus.backend.dto.request.AppointmentCreateRequest;
import com.vidaplus.backend.dto.response.AppointmentResponse;
import com.vidaplus.backend.dto.request.AppointmentUpdateRequest;
import com.vidaplus.backend.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
public class AppointmentController {

    private final AppointmentService service;

    public AppointmentController(AppointmentService service){
        this.service = service;
    }

    @PostMapping
    public AppointmentResponse create(@Valid @RequestBody AppointmentCreateRequest request){
        return service.createAppointment(request);
    }

    @PutMapping("/{id}")
    public AppointmentResponse update(@PathVariable Long id, @Valid @RequestBody AppointmentUpdateRequest request){
        return service.updateAppointment(id, request);
    }

    @PutMapping("/{id}/cancelar")
    public AppointmentResponse cancel(@PathVariable Long id){
        return service.statusCancelAppointment(id);
    }

    @PutMapping("/{id}/concluir")
    public AppointmentResponse ok(@PathVariable Long id){
        return service.statusOkAppointment(id);
    }

    @GetMapping("/{id}")
    public AppointmentResponse findById(@PathVariable Long id){
        return service.findById(id);
    }

    @GetMapping
    public List<AppointmentResponse> getAllAppointments(){
        return service.appointmentList();
    }




}
