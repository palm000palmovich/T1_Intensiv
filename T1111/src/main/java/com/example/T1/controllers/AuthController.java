package com.example.T1.controllers;

import com.example.T1.component.JwtUtil;
import com.example.T1.dto.LoginDto;
import com.example.T1.dto.RegisterDto;
import com.example.T1.exceptions.UserAlreadyRegisteredException;
import com.example.T1.model.User;
import com.example.T1.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping(path = "/auth")
public class AuthController {
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginDto login) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword())
            );
        } catch (Exception e) {
            logger.error("Ошибка при входе: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Incorrect username or password");
        }

        final User user = userService.loadUserByUsername(login.getUsername());
        final String jwt = jwtUtil.generateToken(user);

        logger.info("Полученный jwt: {}", jwt);
        return ResponseEntity.ok().body("Success log-in.");
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDto registerDto) {
        try {
            return ResponseEntity.ok(userService.registerNewUser(registerDto));
        } catch (UserAlreadyRegisteredException exception){
            logger.error(exception.getMessage());
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
}
