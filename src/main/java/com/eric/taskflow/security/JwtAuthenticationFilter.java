package com.eric.taskflow.security;

import com.eric.taskflow.exception.InvalidJwtException;
import com.eric.taskflow.model.User;
import com.eric.taskflow.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/*Esta clase intercepta cada request y valida
* el JWT header Authorization
* Carga el usuario y roles al contexto de Spring Security.*/

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final BlackListService blacklistService;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository, BlackListService blacklistService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.blacklistService = blacklistService;
    }

    // Este filtro se ejecuta UNA vez por request
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        // Leer el header Authorization
        final String authHeader = request.getHeader("Authorization");


        // Si no es Bearer, continúa sin autenticación
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }


        //Se extrae el token sin "Bearer "
        final String token = authHeader.substring(7);



        // =============== 1. Decodificar JWT =================
        var decoded = jwtService.decodeToken(token)
                .orElseThrow(() -> new InvalidJwtException("Token inválido"));

        String jti = decoded.getId();

        // =============== 2. Revisar Blacklist (con JTI) ====
        if (blacklistService.isBlacklisted(jti)) {
            throw new InvalidJwtException("Token revocado (logout realizado)");
        }

        // =============== 3. Validar firma + expiración =====
        if (!jwtService.validateToken(token)) {
            throw new InvalidJwtException("Token inválido o expirado");
        }

        // =============== 4. Extraer username ===============
        String username = decoded.getSubject();
        if (username == null) {
            throw new InvalidJwtException("No se pudo extraer el usuario del token");
        }

        // =============== 5. Cargar usuario ================
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new InvalidJwtException("Usuario del token no existe");
        }

        User user = userOpt.get();

        // =============== 6. Setear autenticación ==========
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            var auth = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }

    }
