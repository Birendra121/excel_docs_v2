package com.avis.security;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.avis.entity.User;
import com.avis.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

	private final UserRepository userRepository;

	
    private final PasswordEncoder passwordEncoder;
	
    public UserDetailsServiceImpl(UserRepository userRepository,@Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder=passwordEncoder;
    }
	
    
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }
	
	public void changePassword(String username, String currentPassword, String newPassword) throws Exception {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new Exception("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new Exception("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
	}

	

