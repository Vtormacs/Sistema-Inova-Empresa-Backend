package com.Inova.Inova.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    @NotBlank
    private String nome;

    @Column
    @NotBlank
    private String descricao;

    private LocalDate inicio;

    private LocalDate fim;

    private LocalDate avaliacaoJurados;

    private LocalDate avaliacaoPopular;

    @ManyToMany
    @JoinTable(
            name = "event_jurados",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "jurado_id")
    )
    @JsonIgnoreProperties({"eventos", "ideia"})
    private Set<UserEntity> jurados;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"evento"})
    private Set<IdeaEntity> ideias = new HashSet<>();

}
