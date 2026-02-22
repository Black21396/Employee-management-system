package net.fadi.ems.service.interfaces;

import net.fadi.ems.model.AuthenticationResponse;
import net.fadi.ems.model.LoginRequest;
import net.fadi.ems.model.RegisterRequest;

public interface AuthenticationServiceInterface {
    public AuthenticationResponse register(RegisterRequest request);

    public AuthenticationResponse login(LoginRequest request);
}
