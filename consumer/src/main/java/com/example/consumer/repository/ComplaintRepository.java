package com.example.consumer.repository;

import org.springframework.data.jpa.repository.Query;

import com.example.consumer.entity.Complaint;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    @Query("SELECT MAX(CAST(c.id AS int)) FROM Complaint c")
    Integer findMaxId();
}

