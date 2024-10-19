package com.enigma.tokonyadia_api.repository;

import com.enigma.tokonyadia_api.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
