package com.workshop.Repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workshop.Entity.Penalty;

@Repository
public interface PenaltyRepo extends JpaRepository<Penalty, UUID> {

}