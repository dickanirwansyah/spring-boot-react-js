package com.app.boot.springbootapppolling.repository;

import com.app.boot.springbootapppolling.model.ChoiceVoteCount;
import com.app.boot.springbootapppolling.model.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long>{

    @Query("SELECT NEW com.app.boot.springbootapppolling.model.ChoiceVoteCount(v.choice.idchoie, count(v.idvote)) FROM Vote v WHERE v.poll.idpoll in :idpolls GROUP BY v.choice.idchoie")
    List<ChoiceVoteCount> countByPollIdpollInGroupByIdchoice(@Param("idpolls") List<Long> idpolls);

    @Query("SELECT NEW com.app.boot.springbootapppolling.model.ChoiceVoteCount(v.choice.idchoie, count(v.idvote)) FROM Vote v WHERE v.poll.idpoll = :idpoll GROUP BY v.choice.idchoie")
    List<ChoiceVoteCount> countByPollIdpollInGroupByIdchoice(@Param("idpoll") Long idpoll);


    @Query("SELECT v FROM Vote v WHERE v.user.idusers = :iduser and v.poll.idpoll in :idpolls")
    List<Vote> findByIdusersAndIdpollIn(@Param("iduser") Long idusers, @Param("idpolls") List<Long> idpolls);


    @Query("SELECT v FROM Vote v WHERE v.user.idusers = :iduser and v.poll.idpoll = :idpolll")
    Vote findByIdusersAndIdpolling(@Param("iduser") Long idusers, @Param("idpolll") Long idpolll);

    @Query("SELECT COUNT(v.idvote) FROM Vote v WHERE v.user.idusers = :iduser")
    long countByIdusers(@Param("iduser") Long iduser);


    @Query("SELECT v.poll.idpoll FROM Vote v WHERE v.user.idusers = :iduser")
    Page<Long> findVotedIdpollByIdusers(@Param("iduser") Long iduser, Pageable pageable);
}
