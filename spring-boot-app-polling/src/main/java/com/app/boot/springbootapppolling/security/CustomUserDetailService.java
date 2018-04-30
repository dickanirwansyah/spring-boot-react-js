package com.app.boot.springbootapppolling.security;

import com.app.boot.springbootapppolling.entity.User;
import com.app.boot.springbootapppolling.exception.ResourceNotException;
import com.app.boot.springbootapppolling.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail)
        throws UsernameNotFoundException{

        User user = userRepository
                .findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(()->
                new UsernameNotFoundException("User Not Found with username or email :" + usernameOrEmail));

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserByIdusers(Long idusers){
        User user = userRepository.findById(idusers).orElseThrow(
                ()-> new ResourceNotException("user", "idusers", idusers));

        return UserPrincipal.create(user);
    }
}
