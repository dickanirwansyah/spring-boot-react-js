package com.app.boot.springbootapppolling.payload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserIdentityAvaibility {

    private Boolean available;

    public UserIdentityAvaibility(){}

    public UserIdentityAvaibility(Boolean available){
        this.available = available;
    }
}
