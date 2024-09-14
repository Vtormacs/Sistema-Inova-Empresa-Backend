package com.Inova.Inova.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
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
}
