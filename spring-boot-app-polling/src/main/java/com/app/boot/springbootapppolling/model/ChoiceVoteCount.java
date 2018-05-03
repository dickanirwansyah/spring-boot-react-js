package com.app.boot.springbootapppolling.model;

public class ChoiceVoteCount {

    private Long idchoice;
    private Long voteCount;

    public ChoiceVoteCount(Long idchoice, Long voteCount){
        this.idchoice = idchoice;
        this.voteCount = voteCount;
    }

    public Long getIdchoice(){
        return idchoice;
    }

    public void setIdchoice(Long idchoice){
        this.idchoice = idchoice;
    }

    public Long getVoteCount(){
        return voteCount;
    }

    public void setVoteCount(Long voteCount){
        this.voteCount = voteCount;
    }
}
