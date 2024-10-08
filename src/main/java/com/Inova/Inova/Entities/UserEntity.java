package com.Inova.Inova.Entities;

import com.Inova.Inova.Entities.Enum.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@JsonIgnoreProperties({"senha", "password", "username", "authorities", "enabled", "credentialsNonExpired", "accountNonExpired", "accountNonLocked"})
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    @NotBlank
    private String nome;

    @Column
    @Email
    @NotBlank
    private String email;

    @Column
    @NotBlank
    private String senha;

    @Column
    private Role role;

    @ManyToOne
    @JsonIgnoreProperties({"colaboradores", "jurados"})
    private IdeaEntity ideia;

    @ManyToMany(mappedBy = "jurados")
    @JsonIgnoreProperties({"jurados", "ideias"})
    private Set<EventEntity> eventos;

    @ManyToMany(mappedBy = "jurados")
    @JsonIgnoreProperties({"jurados", "ideias", "colaboradores"})
    private Set<IdeaEntity> avaliacoes = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_votes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "idea_id")
    )
    @JsonIgnoreProperties({"colaboradores", "jurados", "evento"})
    private Set<IdeaEntity> ideiasVotadas = new HashSet<>();

    public UserEntity(String nome, String email, String senha, Role role) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == Role.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_COLABORADOR"), new SimpleGrantedAuthority("ROLE_AVALIADOR"));
        }
        if (this.role == Role.AVALIADOR) {
            return List.of(new SimpleGrantedAuthority("ROLE_COLABORADOR"), new SimpleGrantedAuthority("ROLE_AVALIADOR"));
        } else return List.of(new SimpleGrantedAuthority("ROLE_COLABORADOR"));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    // associations
}
