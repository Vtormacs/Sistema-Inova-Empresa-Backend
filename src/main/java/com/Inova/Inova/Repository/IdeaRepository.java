package com.Inova.Inova.Repository;

import com.Inova.Inova.Entities.IdeaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IdeaRepository extends JpaRepository<IdeaEntity, UUID> {
}
