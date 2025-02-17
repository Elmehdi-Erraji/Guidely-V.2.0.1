package com.spring.guidely.service;

import com.spring.guidely.entities.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {
    Department createDepartment(Department department);
    List<Department> getAllDepartments();
    Page<Department> getAllDepartments(Pageable pageable);
    Department getDepartmentById(UUID id);
    Department updateDepartment(UUID id, Department updatedDepartment);
    void deleteDepartment(UUID id);
}
