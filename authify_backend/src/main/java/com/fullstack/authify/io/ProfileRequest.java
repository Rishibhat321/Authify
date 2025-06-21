package com.fullstack.authify.io;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileRequest {

    @NotBlank(message = "Name should not be empty")
    private String name;

    @Email(message = "Enter Valid email address")
    @NotNull(message = "Email should not be empty")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    // Will get the 400 bad request if validation fails

}
