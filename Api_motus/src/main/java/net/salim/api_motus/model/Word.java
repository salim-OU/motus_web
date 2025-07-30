package net.salim.api_motus.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "words")
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // ID auto-généré

    @Column(nullable = false)
    private Integer length;             // Longueur du mot

    @Column(name = "difficulty")
    private String difficulty;          // Niveau de difficulté

    @Column(nullable = false)
    private String word;

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Score> scores = new ArrayList<>();         // Relation avec les scores
}