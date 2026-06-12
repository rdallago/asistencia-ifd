package com.instituto.asistencia.security;

import com.instituto.asistencia.entity.Usuario;
import com.instituto.asistencia.repository.UsuarioRepository;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(
            UsuarioRepository usuarioRepository) {

        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String username)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado"));

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getActivo(),
                true,
                true,
                true,
                Collections.singletonList(
                        new SimpleGrantedAuthority(
                                "ROLE_" + usuario.getRol().name()
                        )
                )
        );
    }
}