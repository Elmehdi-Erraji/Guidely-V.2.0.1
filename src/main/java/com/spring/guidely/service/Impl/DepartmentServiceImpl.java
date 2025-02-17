package com.spring.guidely.service.Impl;

import com.spring.guidely.entities.Department;
import com.spring.guidely.repository.DepartmentRepository;
import com.spring.guidely.service.DepartmentService;
import com.spring.guidely.web.error.DepartmentAlreadyExistsException;
import com.spring.guidely.web.error.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Department createDepartment(Department department) {
        Optional<Department> existing = departmentRepository.findByName(department.getName());
        if(existing.isPresent()){
            throw new DepartmentAlreadyExistsException("Department with name '" + department.getName() + "' already exists.");
        }
        return departmentRepository.save(department);
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public Page<Department> getAllDepartments(Pageable pageable) {
        return departmentRepository.findAll(pageable);
    }

    @Override
    public Department getDepartmentById(UUID id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
    }

    @Override
    public Department updateDepartment(UUID id, Department updatedDepartment) {
        Department department = getDepartmentById(id);
        if (!department.getName().equals(updatedDepartment.getName())) {
            Optional<Department> existing = departmentRepository.findByName(updatedDepartment.getName());
            if(existing.isPresent()){
                throw new DepartmentAlreadyExistsException("Department with name '" + updatedDepartment.getName() + "' already exists.");
            }
        }
        department.setName(updatedDepartment.getName());
        return departmentRepository.save(department);
    }

    @Override
    public void deleteDepartment(UUID id) {
        Department department = getDepartmentById(id);
        departmentRepository.delete(department);
    }
}
