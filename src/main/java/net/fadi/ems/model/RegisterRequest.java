package net.fadi.ems.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email darf nicht leer sein")
    @Email(message = "Ungültiges Email-Format")
    private String email;

    @NotBlank(message = "Username darf nicht leer sein")
    @Size(min = 3, max = 20, message = "Username muss zwischen 3 und 20 Zeichen haben")
    private String username;

    @NotBlank(message = "Passwort darf nicht leer sein")
    @Size(min = 5, message = "Passwort muss mindestens 5 Zeichen haben")
    private String password;

    private Role role;
}
