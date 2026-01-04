package com.vidaplus.backend.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record RegisterUserRequest(@NotEmpty(message="Email é obrigatório") String email,
                                  @NotEmpty(message="Senha é obrigatório") String password,
                                  @NotEmpty(message=" Número de registro é obrigatório") String numberRegister,
                                  String role) {
}
