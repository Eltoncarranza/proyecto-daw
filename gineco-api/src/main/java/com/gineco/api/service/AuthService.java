package com.gineco.api.service;

import com.gineco.api.dto.AuthDTOs;
import com.gineco.api.entity.Usuario;
import com.gineco.api.exception.BusinessException;
import com.gineco.api.exception.ResourceNotFoundException;
import com.gineco.api.repository.UsuarioRepository;
import com.gineco.api.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthDTOs.LoginResponse login(AuthDTOs.LoginRequest request) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        Usuario usuario = usuarioRepo.findByUsername(request.getUsername()).orElseThrow();

        return new AuthDTOs.LoginResponse(
            token, refreshToken,
            usuario.getUsername(),
            usuario.getNombreCompleto(),
            usuario.getRol().name()
        );
    }

    @Transactional
    public void cambiarPassword(String username, AuthDTOs.CambiarPasswordRequest request) {
        Usuario usuario = usuarioRepo.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
            throw new BusinessException("La contraseña actual es incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(request.getPasswordNueva()));
        usuarioRepo.save(usuario);
    }

    @Transactional
    public void crearUsuarioInicial() {
        // Crea el doctor principal si no existe
        if (!usuarioRepo.existsByUsername("doctor")) {
            Usuario admin = Usuario.builder()
                .username("doctor")
                .password(passwordEncoder.encode("gineco2025"))
                .nombreCompleto("Dr. Ginecólogo")
                .rol(Usuario.Rol.DOCTOR)
                .build();
            usuarioRepo.save(admin);
        }
    }
}
