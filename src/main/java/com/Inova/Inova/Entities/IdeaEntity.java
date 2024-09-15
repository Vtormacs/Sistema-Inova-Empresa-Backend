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
    @JsonIgnoreProperties({"senha", "eventos", "ideia", "password", "username", "authorities", "enabled", "credentialsNonExpired", "accountNonExpired", "accountNonLocked"})
    private Set<UserEntity> colaboradores;

    @ManyToOne
    private EventEntity evento;

}
