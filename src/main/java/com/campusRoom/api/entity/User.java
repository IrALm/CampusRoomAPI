package com.campusRoom.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName; // prénom de l'utilisateur
    private String lastName;  // nom de l'utilisateur

    @Column(unique = true)
    private String email;     // identifiant unique pour login / notifications

    @Enumerated(EnumType.STRING)
    private Role role;        // Role. Ex: STUDENT, TEACHER

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();
}
