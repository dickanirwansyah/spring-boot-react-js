package com.app.boot.springbootapppolling.entity;

import com.app.boot.springbootapppolling.model.DateAudit;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@Data
@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "username"
        }),
        @UniqueConstraint(columnNames = {
                "email"
        })
})
public class User extends DateAudit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idusers;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 15)
    @NotBlank
    @Column(name = "username", nullable = false)
    private String username;

    @Size(max = 40)
    @NotBlank
    @Email
    @Column(name = "email", nullable = false)
    private String email;

    @Size(max = 100)
    @NotBlank
    @Column(name = "password")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns =
    @JoinColumn(name = "idusers", referencedColumnName = "idusers"), inverseJoinColumns =
    @JoinColumn(name = "idroles", referencedColumnName = "idroles"))
    private Set<Role> roles = new HashSet<>();

    public User(){}

    public User(String name, String username, String email, String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
