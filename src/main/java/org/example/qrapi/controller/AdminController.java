package org.example.qrapi.controller;
import com.google.zxing.WriterException;
import org.apache.tomcat.websocket.AuthenticationException;
import org.example.qrapi.model.user.User;
import org.example.qrapi.model.user.UserDto;
import org.example.qrapi.services.user.UserService;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;



    @GetMapping(value = "/users")
    public @ResponseBody List<User> users() {
        return userService.getAllUsers();
    }


    // Assumes that only username and password can be updated
    @PutMapping(value = "/users/{id}")
    public @ResponseBody ResponseEntity<String> updateById(@RequestBody UserDto payload, @PathVariable Long id) {
        userService.updateById(payload, id);

        return ResponseEntity.ok("User successfully updated");
    }

    @DeleteMapping(value = "/users/{id}")
    public @ResponseBody ResponseEntity<String> deleteById(@PathVariable Long id) {
        userService.deleteById(id);

        return ResponseEntity.ok("User successfully deleted");
    }
}
