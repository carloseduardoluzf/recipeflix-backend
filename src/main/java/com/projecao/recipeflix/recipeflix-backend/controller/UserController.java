package com.projecao.recipeflix.controller;
import com.projecao.recipeflix.dto.UserRegisterDTO;
import com.projecao.recipeflix.model.User;
import com.projecao.recipeflix.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register/user")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegisterDTO userDTO) {
        // Verifica se já existe um usuário com o mesmo username
        var existingUser = userRepository.findByUsername(userDTO.username());
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Já existe um usuário com o mesmo username.");
        }

        User newUser = new User(userDTO.username(), passwordEncoder.encode(userDTO.password()), userDTO.authorName());
        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        // Busca o usuário pelo ID no banco de dados
        User user = userRepository.findById(id).orElse(null);

        // Verifica se o usuário foi encontrado
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }
    }

    @PostMapping("/loginw")
    public ResponseEntity<?> loginUserOld(@RequestBody User user) {
        User existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser == null || !passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário ou senha inválidos.");
        }

        return ResponseEntity.ok("{\"message\": \"Login bem-sucedido para " + existingUser.getUsername() + "\"}");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        User existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser == null || !passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário ou senha inválidos.");
        }

        // Gera o token JWT
        String token = Jwts.builder()
                .setSubject(existingUser.getUsername())
                .claim("userId", existingUser.getId())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS))) // Define a expiração do token
                .signWith(Keys.hmacShaKeyFor(secret.getBytes())) // Assina o token com a chave secreta
                .compact();

        // Retorna o token JWT junto com a resposta
        return ResponseEntity.ok(Map.of("token", token));
    }


}
