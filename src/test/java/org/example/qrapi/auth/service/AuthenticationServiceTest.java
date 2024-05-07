package org.example.qrapi.auth.service;

import org.example.qrapi.auth.model.AuthenticationRequest;
import org.example.qrapi.auth.model.AuthenticationResponse;
import org.example.qrapi.auth.model.RegisterRequest;
import org.example.qrapi.model.user.Role;
import org.example.qrapi.model.user.RoleName;
import org.example.qrapi.model.user.User;
import org.example.qrapi.repositories.user.RoleRepository;
import org.example.qrapi.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void register_NewUser_SuccessfullyRegistered() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("test@example.com", "password");
        Role userRole = new Role(1L, RoleName.USER);
        when(roleRepository.findByRoleName(RoleName.USER)).thenReturn(userRole);
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(null);

        // When
        authenticationService.register(registerRequest);

        // Then
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ExistingUser_ThrowsException() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("existing@example.com", "password");
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(new User());

        // When & Then
        assertThrows(Exception.class, () -> authenticationService.register(registerRequest));
    }

    @Test
    void login_ValidCredentials_ReturnsAuthenticationResponse() {
        // Given
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("test@example.com", "password");
        User user = new User();
        user.setEmail(authenticationRequest.getEmail());
        when(userRepository.findByEmail(authenticationRequest.getEmail())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        // When
        AuthenticationResponse response = authenticationService.login(authenticationRequest);

        // Then
        assertNotNull(response);
        assertNotNull(response.getToken());
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        // Given
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("nonexistent@example.com", "password");
        when(userRepository.findByEmail(authenticationRequest.getEmail())).thenReturn(null);

        // When & Then
        assertThrows(Exception.class, () -> authenticationService.login(authenticationRequest));
    }

}
