package com.enigma.tokonyadia_api.repository;

import com.enigma.tokonyadia_api.entity.Product;
import com.enigma.tokonyadia_api.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, String>, JpaSpecificationExecutor<Store> {

    @Query("select p from Product p where p.store.id = :storeId")
    List<Product> findProductsByStoreId(String storeId);
}
