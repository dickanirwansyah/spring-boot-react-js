package com.app.boot.springbootapppolling.model;

import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "choice")
@EqualsAndHashCode
public class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idchoie;

    @NotBlank
    @Size(max = 40)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idpoll", nullable = false)
    private Poll poll;

    public Choice(){}

    public Choice(String text){
        this.text = text;
    }

    public Long getIdchoie(){
        return idchoie;
    }

    public void setIdchoie(Long idchoie){
        this.idchoie = idchoie;
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll){
        this.poll = poll;
    }
}
