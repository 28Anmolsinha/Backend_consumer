package com.example.consumer.repository;



import com.example.consumer.entity.Complaint;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
}

