package org.example.qrapi.services.user;

import org.example.qrapi.model.qr.QrCode;
import org.example.qrapi.model.user.Role;
import org.example.qrapi.model.user.RoleName;
import org.example.qrapi.model.user.User;
import org.example.qrapi.model.user.UserDto;
import org.example.qrapi.repositories.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void addQrToUser_UserExists_AddsQrCode() {
        // Given
        User user = new User();
        QrCode qrCode = new QrCode();
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(user);

        // When
        userService.addQrToUser(qrCode, email);

        // Then
        assertTrue(user.getQrCodes().contains(qrCode));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void saveUser_UserSavedSuccessfully() {
        // Given
        User user = new User();

        // When
        userService.saveUser(user);

        // Then
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findByEmailAddress_ExistingEmail_ReturnsUser() {
        // Given
        String email = "test@example.com";
        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(user);

        // When
        User result = userService.findByEmailAddress(email);

        // Then
        assertEquals(user, result);
    }

    @Test
    void getAllUsers_ReturnsListOfUsers() {
        // Given
        List<User> userList = new ArrayList<>();
        userList.add(new User());
        userList.add(new User());
        List<Role> userRole = new ArrayList<>();
        userRole.add(Role.builder().roleName(RoleName.USER).build());
        when(userRepository.findAllByRoles(userRole)).thenReturn(userList);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertEquals(userList, result);
    }

    @Test
    void updateById_UserUpdatedSuccessfully() {
        // Given
        Long id = 1L;
        UserDto payload = new UserDto();
        payload.setEmail("new@example.com");
        payload.setPassword("newPassword");
        User user = new User();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // When
        userService.updateById(payload, id);

        // Then
        assertEquals("new@example.com", user.getEmail());
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deleteById_UserDeletedSuccessfully() {
        // Given
        Long id = 1L;
        User user = new User();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        // When
        userService.deleteById(id);

        // Then
        verify(userRepository, times(1)).deleteById(id);
    }
}
