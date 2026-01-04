package com.vidaplus.backend.controller;

import com.vidaplus.backend.dto.request.LoginRequest;
import com.vidaplus.backend.dto.request.RegisterUserRequest;
import com.vidaplus.backend.dto.response.LoginResponse;
import com.vidaplus.backend.dto.response.RegisterUserResponse;
import com.vidaplus.backend.model.User;
import com.vidaplus.backend.model.UserRole;
import com.vidaplus.backend.repository.UserRepository;
import com.vidaplus.backend.config.TokenConfig;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          TokenConfig tokenConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        System.out.println("=== TENTATIVA DE LOGIN ===");
        System.out.println("Email recebido: " + request.email());
        System.out.println("Senha recebida: " + request.password());

        // Verificar se usuário existe
        var userOpt = userRepository.findByEmail(request.email());
        if (userOpt.isEmpty()) {
            System.out.println("ERRO: Usuário não encontrado no banco!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuário não encontrado"));
        }

        User user = userOpt.get();
        System.out.println("Usuário encontrado: " + user.getEmail());
        System.out.println("Senha no banco (hash): " + user.getPassword());

        // Verificar senha manualmente
        boolean senhaCorreta = passwordEncoder.matches(request.password(), user.getPassword());
        System.out.println("Senha correta? " + senhaCorreta);

        if (!senhaCorreta) {
            System.out.println("ERRO: Senha incorreta!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Senha incorreta"));
        }

        try {
            UsernamePasswordAuthenticationToken userAndPass =
                    new UsernamePasswordAuthenticationToken(request.email(), request.password());

            System.out.println("Tentando autenticar com AuthenticationManager...");
            Authentication authentication = authenticationManager.authenticate(userAndPass);

            System.out.println("Autenticação bem-sucedida!");
            User authenticatedUser = (User) authentication.getPrincipal();
            String token = tokenConfig.generateToken(authenticatedUser);

            System.out.println("Token gerado: " + token);
            return ResponseEntity.ok(new LoginResponse(token));

        } catch (BadCredentialsException e) {
            System.out.println("ERRO: BadCredentialsException - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciais inválidas"));
        } catch (Exception e) {
            System.out.println("ERRO inesperado: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao processar login"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserRequest request) {
        System.out.println("=== TENTATIVA DE REGISTRO ===");
        System.out.println("Email: " + request.email());

        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email já cadastrado"));
        }

        User newUser = new User();
        newUser.setNumberRegister(request.numberRegister());
        newUser.setEmail(request.email());
        newUser.setPassword(passwordEncoder.encode(request.password()));

        // Define o role
        UserRole role = UserRole.PATIENT; // Padrão PATIENT
        if (request.role() != null) {
            try {
                role = UserRole.valueOf(request.role().toUpperCase());
            } catch (IllegalArgumentException e) {
                role = UserRole.PATIENT;
            }
        }
        newUser.setRole(role);

        userRepository.save(newUser);
        System.out.println("Usuário salvo com role: " + role);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new RegisterUserResponse(newUser.getNumberRegister(), newUser.getEmail())
        );
    }
}
