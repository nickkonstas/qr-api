package org.example.qrapi.auth.service;


import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.example.qrapi.auth.model.AuthenticationRequest;
import org.example.qrapi.auth.model.AuthenticationResponse;
import org.example.qrapi.auth.model.RegisterRequest;
import org.example.qrapi.model.user.Role;
import org.example.qrapi.model.user.RoleName;
import org.example.qrapi.model.user.User;
import org.example.qrapi.repositories.user.RoleRepository;
import org.example.qrapi.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
// todo: extract an interface implemented by this class
public class AuthenticationService {
    private static  String ADMIN_EMAIL;

    @Value("${admin.email.address}")
    public void setAdminEmail(String email) {
        ADMIN_EMAIL = email;
    }

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public void register(RegisterRequest request) throws AuthenticationException {
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new AuthenticationException("User already exists");
        }
        Role role;
        if (request.getEmail().equals(ADMIN_EMAIL)) {
            role = roleRepository.findByRoleName(RoleName.ADMIN);
        }
        else {
            role = roleRepository.findByRoleName(RoleName.USER);
        }

        //todo: refactor this empty initialization
        User user = User.builder()
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .roles(new ArrayList<>())
                .build();
        user.addRole(role);
        userRepository.save(user);
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new UsernameNotFoundException("Wrong credentials");
        }
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public UserDetails retrieveUserFromJwt() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String username = userDetails.getUsername();
        System.out.println("Username : " + username);
        return userDetails;
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByEmail(username);
//
//        if (user != null) {
//            return new org.springframework.security.core.userdetails.User(user.getEmail(),
//                    user.getPassword(),
//                    user.getAuthorities());
//        }else{
//            throw new UsernameNotFoundException("Invalid username or password.");
//        }
//
////        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
////        appUser.getRoles().forEach(role -> {
////            authorities.add(new SimpleGrantedAuthority(role.getName()));
////        });
//    }

}
