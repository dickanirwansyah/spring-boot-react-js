package com.app.boot.springbootapppolling.payload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ChoiceResponse {

    private Long idchoie;
    private String text;
    private long voteCount;
}
