package com.awy.common.security.oauth2.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public  class LoginDTO{
    private String username;
    private String password;
}
