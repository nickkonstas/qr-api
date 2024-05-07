package org.example.qrapi.controller;

import org.example.qrapi.model.user.User;
import org.example.qrapi.model.user.UserDto;
import org.example.qrapi.services.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminController adminController;


    @Test
    void users_ReturnsListOfUsers() {
        // Given
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        User user2 = new User();
        userList.add(user1);
        userList.add(user2);
        when(userService.getAllUsers()).thenReturn(userList);

        // When
        List<User> result = adminController.users();

        // Then
        assertEquals(userList, result);
    }

    @Test
    void updateById_UserUpdated_ReturnsOkResponse() {
        // Given
        UserDto payload = new UserDto();
        Long id = 1L;

        // When
        ResponseEntity<String> response = adminController.updateById(payload, id);

        // Then
        assertEquals("User successfully updated", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).updateById(payload, id);
    }

    @Test
    void deleteById_UserDeleted_ReturnsOkResponse() {
        // Given
        Long id = 1L;

        // When
        ResponseEntity<String> response = adminController.deleteById(id);

        // Then
        assertEquals("User successfully deleted", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).deleteById(id);
    }
}
