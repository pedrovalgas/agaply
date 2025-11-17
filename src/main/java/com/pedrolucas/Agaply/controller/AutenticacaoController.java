package com.pedrolucas.Agaply.controller;

import com.pedrolucas.Agaply.dto.autenticacao.AuthenticationDTO;
import com.pedrolucas.Agaply.dto.autenticacao.LoginResponseDTO;
import com.pedrolucas.Agaply.dto.autenticacao.RegisterDTO;
import com.pedrolucas.Agaply.infra.security.TokenService;
import com.pedrolucas.Agaply.model.Usuario;
import com.pedrolucas.Agaply.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data){
        if(this.repository.findByLogin(data.login()) != null) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.senha());

        Usuario newUser = new Usuario(
                null,
                data.login(),
                encryptedPassword,
                data.role()
        );

        this.repository.save(newUser);

        return ResponseEntity.ok().build();
    }

}
