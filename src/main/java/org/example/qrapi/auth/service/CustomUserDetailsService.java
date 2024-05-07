package org.example.qrapi.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.qrapi.model.user.User;
import org.example.qrapi.repositories.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);

        if (user != null) {
            return new org.springframework.security.core.userdetails.User(user.getEmail(),
                    user.getPassword(),
                    user.isEnabled(),
                    user.isCredentialsNonExpired(),
                    user.isAccountNonExpired(),
                    user.isAccountNonLocked(),
                    user.getAuthorities());
        }else{
            throw new UsernameNotFoundException("Invalid username or password.");
        }

//        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
//        appUser.getRoles().forEach(role -> {
//            authorities.add(new SimpleGrantedAuthority(role.getName()));
//        });
    }
}


