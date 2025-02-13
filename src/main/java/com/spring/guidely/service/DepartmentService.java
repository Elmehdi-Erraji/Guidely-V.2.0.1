package com.spring.guidely.service;

import com.spring.guidely.entities.Department;
import com.spring.guidely.repository.DepartmentRepository;
import com.spring.guidely.web.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(UUID id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
    }

    public Department updateDepartment(UUID id, Department updatedDepartment) {
        Department department = getDepartmentById(id);
        department.setName(updatedDepartment.getName());
        return departmentRepository.save(department);
    }

    public void deleteDepartment(UUID id) {
        Department department = getDepartmentById(id);
        departmentRepository.delete(department);
    }
}
