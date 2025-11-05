package com.attendance.reportservice.util;

import com.attendance.reportservice.entity.AttendanceReport;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SpecificationBuilder {

    public static Specification<AttendanceReport> build(Long employeeId, String month) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (employeeId != null) {
                predicates.add(criteriaBuilder.equal(root.get("employeeId"), employeeId));
            }
            if (month != null) {
                predicates.add(criteriaBuilder.equal(root.get("month"), month));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
