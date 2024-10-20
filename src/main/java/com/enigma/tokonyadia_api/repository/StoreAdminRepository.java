package com.enigma.tokonyadia_api.repository;

import com.enigma.tokonyadia_api.entity.StoreAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StoreAdminRepository extends JpaRepository<StoreAdmin, String>, JpaSpecificationExecutor<StoreAdmin> {
}
