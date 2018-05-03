package com.app.boot.springbootapppolling.payload;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@EqualsAndHashCode
public class PollRequest {

    @NotBlank
    @Size(max = 140)
    private String question;

    @NotNull
    @Size(min = 2, max = 6)
    @Valid
    private List<ChoiceRequest> choiceRequests = new ArrayList<>();

    @NotNull
    @Valid
    private PollLength pollLength;
}
