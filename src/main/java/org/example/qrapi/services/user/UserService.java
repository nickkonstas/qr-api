package org.example.qrapi.services.user;

import org.example.qrapi.model.qr.QrCode;
import org.example.qrapi.model.user.User;
import org.example.qrapi.model.user.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    public void addQrToUser(QrCode qrCode, String email);
    public void saveUser(User user);

    User findByEmailAddress(String username);

    List<User> getAllUsers();

    void updateById(UserDto payload, Long id);

    void deleteById(Long id);
}
