package com.app.boot.springbootapppolling.payload;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
@Getter
@Setter
@EqualsAndHashCode
public class VoteRequest {

    @NotNull
    private Long idchoie;

}
