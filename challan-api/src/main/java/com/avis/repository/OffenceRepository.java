package com.avis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avis.util.Offence;

public interface OffenceRepository extends JpaRepository<Offence, Long>{

}
