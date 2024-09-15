package com.Inova.Inova.Entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "idea")
public class IdeaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    private String nome;

    private String impacto;

    private BigDecimal custoEstimado;

    private String descricao;

    @OneToMany
    @JsonIgnoreProperties({"senha", "eventos", "ideia", "avaliacoes" ,"password", "username", "authorities", "enabled", "credentialsNonExpired", "accountNonExpired", "accountNonLocked"})
    private Set<UserEntity> colaboradores;

    @ManyToOne
    @JoinColumn(name = "evento_id")
    @JsonIgnoreProperties({"ideias", "jurados"})
    private EventEntity evento;

    @ManyToMany
    @JoinTable(
            name = "ideia_jurados",
            joinColumns = @JoinColumn(name = "ideia_id"),
            inverseJoinColumns = @JoinColumn(name = "jurado_id")
    )
    @JsonIgnoreProperties({"eventos", "ideia"})
    private Set<UserEntity> jurados = new HashSet<>();

}
