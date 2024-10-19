package com.enigma.tokonyadia_api.specification;

import com.enigma.tokonyadia_api.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {
    public static Specification<Product> getSpecification(String q) {
        return  (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(q)) return criteriaBuilder.conjunction();
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("price")), "%" + q + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + q + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("stock")), "%" + q + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + q + "%")
            ));
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
    }
}
