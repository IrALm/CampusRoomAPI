package com.campusRoom.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // Nom / code de la salle
    private Integer capacity;   // Capacité maximale
    private String location;    // Emplacement (bâtiment, étage)

    @ElementCollection
    private List<String> equipment = new ArrayList<>(); // Liste équipements (projecteur, PC…)

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();
}
