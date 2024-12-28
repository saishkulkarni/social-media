package com.s13sh.social_media.dto;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Size(min = 3, max = 10,message = "First name should be between 3 and 10 characters")
    private String firstname;
    @Size(min = 3, max = 20,message = "Last name should be between 3 and 20 characters")
    private String lastname;
    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, one special character, and be at least 8 characters long")
    private String password;
    @Transient
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, one special character, and be at least 8 characters long")
    private String confirmpassword;
    @Size(min = 5, max = 15,message = "Username should be between 5 and 15 characters")
    private String username;
    @NotNull(message = "Mobile number is required")
    @DecimalMin(value = "6000000000", message = "Mobile number must be 10 digits")
    @DecimalMax(value = "9999999999", message = "Mobile number must be 10 digits")
    private long mobile;
    @Size(min = 3, max = 100,message = "Bio should be between 3 and 100 characters")
    private String bio;
    @Transient
    private MultipartFile profilepicture;
    private String profilepictureurl;
    @NotNull(message = "Gender is required")
    private String gender;
    @NotNull(message = "Date of birth is required")
    @Past(message = "Enter Proper DOB")
    private LocalDate dob;
    private int otp;
    private boolean verified;
}
