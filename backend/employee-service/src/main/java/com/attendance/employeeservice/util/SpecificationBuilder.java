package com.attendance.employeeservice.util;

import com.attendance.employeeservice.entity.Employee;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;

public class SpecificationBuilder {

    public static Specification<Employee> build(String name, String department, String designation, String email) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }
            if (department != null) {
                predicates.add(criteriaBuilder.like(root.get("department"), "%" + department + "%"));
            }
            if (designation != null) {
                predicates.add(criteriaBuilder.like(root.get("designation"), "%" + designation + "%"));
            }
            if (email != null) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + email + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
