package com.Inova.Inova.Repository;

import com.Inova.Inova.Entities.IdeaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface IdeaRepository extends JpaRepository<IdeaEntity, UUID> {

    @Query(value = "SELECT * FROM idea ORDER BY votos DESC LIMIT 10", nativeQuery = true)
    List<IdeaEntity> top10Votados();
}
