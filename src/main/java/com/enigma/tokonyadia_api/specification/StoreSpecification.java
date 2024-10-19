package com.enigma.tokonyadia_api.specification;

import com.enigma.tokonyadia_api.entity.Store;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StoreSpecification {
    public static Specification<Store> getSpecification(String q) {
        return  (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(q)) return criteriaBuilder.conjunction();
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("noSiup")), "%" + q + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + q + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), "%" + q + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), "%" + q + "%")
            ));
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
