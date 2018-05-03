package com.app.boot.springbootapppolling.payload;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Getter
@Setter
@EqualsAndHashCode
public class ChoiceRequest {

    @NotNull
    @Size(max = 40)
    public String text;


}
