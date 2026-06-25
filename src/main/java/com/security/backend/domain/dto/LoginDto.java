package com.security.backend.domain.dto;

import lombok.Data;

@Data
public class LoginDto {

    private String credentials;


    private String principal;
}
