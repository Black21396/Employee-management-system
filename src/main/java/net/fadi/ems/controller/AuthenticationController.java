package net.fadi.ems.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.fadi.ems.model.AuthenticationResponse;
import net.fadi.ems.model.LoginRequest;
import net.fadi.ems.model.RegisterRequest;
import net.fadi.ems.service.JwtAuthenticationService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtAuthenticationService authService;

    /**
     * Registrierungs-Endpoint.
     * URL: POST /api/auth/register
     *
     * @Valid → Aktiviert Validierung des @RequestBody
     *        Wenn Validierung fehlschlägt → MethodArgumentNotValidException
     *        → 400 Bad Request mit Fehlerdetails
     *
     *        ResponseEntity → gibt uns Kontrolle über HTTP-Status, Headers, Body
     *
     * @return 201 Created + AuthResponse (Token + Rolle)
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthenticationResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Login-Endpoint.
     * URL: POST /api/auth/login
     *
     * Warum kein @Valid hier?
     * LoginRequest hat keine @NotBlank Annotationen, weil
     * email UND username beide optional sind.
     * Die Validierung machen wir im AuthService.
     *
     * @return 200 OK + AuthResponse (Token + Rolle)
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        AuthenticationResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
