package com.Inova.Inova.Repository;

import com.Inova.Inova.Entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EventRepository extends JpaRepository<EventEntity, UUID> {
}
