package com.enigma.tokonyadia_api.controller;

import com.enigma.tokonyadia_api.dto.response.UserResponse;
import com.enigma.tokonyadia_api.service.UserService;
import com.enigma.tokonyadia_api.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(path = "me")
    public ResponseEntity<?> getSelfInfo() {
        UserResponse userResponse = userService.getAuthentication();
        return ResponseUtil.buildResponse(HttpStatus.OK, "Success fetch user info", userResponse);
    }
}
