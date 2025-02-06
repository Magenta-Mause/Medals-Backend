package com.medals.medalsbackend.entity.users;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "login_entry")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginEntry {
    @Id
    @Column(unique = true, nullable = false)
    private String email;
    @Column
    private String password;

    @OneToMany(mappedBy = "loginEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserEntity> users = new ArrayList<>();

    public void addUser(UserEntity user) {
        user.setLoginEntry(this);
        users.add(user);
    }

    public void removeUser(UserEntity user) {
        user.setLoginEntry(null);
        users.remove(user);
    }
}
