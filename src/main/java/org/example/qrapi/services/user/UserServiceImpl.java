package org.example.qrapi.services.user;

import lombok.RequiredArgsConstructor;
import org.example.qrapi.model.qr.QrCode;
import org.example.qrapi.model.user.Role;
import org.example.qrapi.model.user.RoleName;
import org.example.qrapi.model.user.User;
import org.example.qrapi.model.user.UserDto;
import org.example.qrapi.repositories.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public void addQrToUser(QrCode qrCode, String email) {
        User user = userRepository.findByEmail(email);
        user.getQrCodes().add(qrCode);
        saveUser(user);

    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User findByEmailAddress(String username) {
        return userRepository.findByEmail(username);
    }

    @Override
    public List<User> getAllUsers() {
        List<Role> userRole = new ArrayList<>();
        userRole.add(Role.builder().roleName(RoleName.USER).build());
        return userRepository.findAllByRoles(userRole);
    }

    @Override
    public void updateById(UserDto payload, Long id) {
        User user = userRepository.findById(id).orElse(null);
        if(user == null) {
            LOGGER.info("User with id {} doesn't exist", id);
            return;
        }
        if (payload.getEmail() != null) {
            user.setEmail(payload.getEmail());
        }
        if (payload.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(payload.getPassword()));
        }
        saveUser(user);
    }

    @Override
    public void deleteById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if(user == null) {
            LOGGER.info("User with id {} doesn't exist", id);
            return;
        }
        LOGGER.info("Deleting user with id {}", id);
        userRepository.deleteById(id);
    }
}
