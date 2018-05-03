package com.app.boot.springbootapppolling.repository;

import com.app.boot.springbootapppolling.model.ChoiceVoteCount;
import com.app.boot.springbootapppolling.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long>{

    @Query("SELECT NEW com.app.boot.springbootapppolling.model.ChoiceVoteCount(v.choice.idchoie, count(v.idvote)) FROM Vote v WHERE v.poll.idpoll in :idpolls GROUP BY v.choice.idchoie")
    List<ChoiceVoteCount> countByPollIdpollInGroupByIdchoice(@Param("idpolls") List<Long> idpolls);

    
}
