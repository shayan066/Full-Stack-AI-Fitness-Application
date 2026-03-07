package com.fitness.userservice.controller;

import com.fitness.userservice.model.request.RegisterRequest;
import com.fitness.userservice.model.response.UserResponse;
import com.fitness.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String userId){
        try{
            return new ResponseEntity<>(userService.getUserProfile(userId), HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest request){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(request));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
