package com.gineco.api.service;

import com.gineco.api.dto.GinecoDTOs; // Cambio: Usar GinecoDTOs
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

    public GinecoDTOs.LoginResponse login(GinecoDTOs.LoginRequest request) {
        // 1. Autenticar con Spring Security
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 2. Cargar detalles y generar Token
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        // 3. Obtener datos adicionales del usuario para la respuesta
        Usuario usuario = usuarioRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // 4. Construir respuesta (Ajustado a los campos de GinecoDTOs.LoginResponse)
        GinecoDTOs.LoginResponse response = new GinecoDTOs.LoginResponse();
        response.setToken(token);
        response.setUsername(usuario.getUsername());
        response.setNombre(usuario.getNombreCompleto());
        response.setRol(usuario.getRol().name());

        return response;
    }

    @Transactional
    public void cambiarPassword(String username, GinecoDTOs.CambiarPasswordRequest request) {
        Usuario usuario = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Nota: Verifica que los nombres de campos coincidan con GinecoDTOs (actualPassword vs passwordActual)
        if (!passwordEncoder.matches(request.getActualPassword(), usuario.getPassword())) {
            throw new BusinessException("La contraseña actual es incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(request.getNuevoPassword()));
        usuarioRepo.save(usuario);
    }

    @Transactional
    public void crearUsuarioInicial() {
        if (!usuarioRepo.existsByUsername("doctor")) {
            Usuario admin = new Usuario(); // Usamos constructor si builder da problemas
            admin.setUsername("doctor");
            admin.setPassword(passwordEncoder.encode("gineco2025"));
            admin.setNombreCompleto("Dr. Ginecólogo");
            admin.setRol(Usuario.Rol.DOCTOR);
            admin.setActivo(true);
            usuarioRepo.save(admin);
        }
    }
}