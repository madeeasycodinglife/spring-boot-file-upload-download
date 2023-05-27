package com.madeeasy.repository;

import com.madeeasy.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepo extends JpaRepository<Files,Long> {
}
