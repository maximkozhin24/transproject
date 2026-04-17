package com.logistics.logisticsapp.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ClientRequestDto {
    @NotBlank(message = "Name must not be empty")
    private String name;
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email must not be empty")
    private String email;
    @Size(max = 20, message = "Phone must be at most 20 characters")
    @NotBlank(message = "Phone must not be empty")
    private String phone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}