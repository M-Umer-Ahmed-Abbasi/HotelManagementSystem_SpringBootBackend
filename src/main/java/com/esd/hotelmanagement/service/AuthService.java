package com.esd.hotelmanagement.service;

import com.esd.hotelmanagement.dto.*;
import com.esd.hotelmanagement.entity.LoyaltyAccount;
import com.esd.hotelmanagement.entity.Role;
import com.esd.hotelmanagement.entity.User;
import com.esd.hotelmanagement.exception.DuplicateResourceException;
import com.esd.hotelmanagement.repository.LoyaltyAccountRepository;
import com.esd.hotelmanagement.repository.UserRepository;
import com.esd.hotelmanagement.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for authentication operations.
 * 
 * @author Umer
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Set default role to GUEST if not specified
        Role role = request.getRole() != null ? request.getRole() : Role.GUEST;

        // Create user
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(role)
                .enabled(true)
                .build();

        user = userRepository.save(user);

        // Create loyalty account for guests
        if (role == Role.GUEST) {
            LoyaltyAccount loyaltyAccount = LoyaltyAccount.builder()
                    .user(user)
                    .points(0)
                    .tier("BRONZE")
                    .build();
            loyaltyAccountRepository.save(loyaltyAccount);
        }

        // Generate JWT token
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        // Get user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        // Generate JWT token
        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }
}
