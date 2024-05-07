package org.example.qrapi.auth.controller;

import org.example.qrapi.auth.model.AuthenticationRequest;
import org.example.qrapi.auth.model.AuthenticationResponse;
import org.example.qrapi.auth.model.RegisterRequest;
import org.example.qrapi.auth.service.AuthenticationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void register_SuccessfulRegistration_ReturnsCreatedResponse() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("username", "password");

        // When
        ResponseEntity<AuthenticationResponse> response = authController.register(registerRequest);

        // Then
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(authenticationService, times(1)).register(registerRequest);
    }

    

    @Test
    void login_SuccessfulLogin_ReturnsOkResponse() {
        // Given
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("username", "password");
        AuthenticationResponse authenticationResponse = new AuthenticationResponse("token");

        when(authenticationService.login(authenticationRequest)).thenReturn(authenticationResponse);

        // When
        ResponseEntity<AuthenticationResponse> response = authController.login(authenticationRequest);

        // Then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(authenticationResponse, response.getBody());
    }

    @Test
    void login_FailedLogin_ReturnsUnauthorizedResponse() {
        // Given
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("username", "password");
        when(authenticationService.login(authenticationRequest)).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // When & Then
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, assertThrows(ResponseStatusException.class,
                () -> authController.login(authenticationRequest)).getStatusCode());
    }
}