package com.vidaplus.backend.config;

import com.vidaplus.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthConfig implements UserDetailsService {

    private final UserRepository userRepository;

    public AuthConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername chamado com: " + username);

        return userRepository.findByEmail(username)
                .map(user -> {
                    System.out.println("Usuário encontrado: " + user.getEmail());
                    System.out.println("Senha hash: " + user.getPassword());
                    return (UserDetails) user;
                })
                .orElseThrow(() -> {
                    System.out.println("Usuário não encontrado: " + username);
                    return new UsernameNotFoundException("Usuário não encontrado: " + username);
                });
    }
}