package com.app.boot.springbootapppolling.model;

import com.app.boot.springbootapppolling.entity.User;

import javax.persistence.*;

@Entity
@Table(name = "votes")
public class Vote extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idvote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idpoll", nullable = false)
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idchoice", nullable = false)
    private Choice choice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusers", nullable = false)
    private User user;

    public Long getIdvote(){
        return idvote;
    }

    public void setIdvote(Long idvote){
        this.idvote = idvote;
    }

    public Poll getPoll(){
        return poll;
    }

    public void setPoll(Poll poll){
        this.poll = poll;
    }

    public Choice getChoice() {
        return choice;
    }

    public void setChoice(Choice choice) {
        this.choice = choice;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }
}
