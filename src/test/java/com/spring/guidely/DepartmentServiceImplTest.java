package com.spring.guidely;

import com.spring.guidely.entities.Department;
import com.spring.guidely.repository.DepartmentRepository;
import com.spring.guidely.service.DepartmentService;
import com.spring.guidely.service.Impl.DepartmentServiceImpl;
import com.spring.guidely.web.error.DepartmentAlreadyExistsException;
import com.spring.guidely.web.error.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    private DepartmentService departmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        departmentService = new DepartmentServiceImpl(departmentRepository);
    }

    // Test createDepartment() method
    @Test
    void createDepartment_UniqueName_Success() {
        Department department = new Department();
        department.setName("HR");

        when(departmentRepository.findByName("HR")).thenReturn(Optional.empty());
        when(departmentRepository.save(department)).thenReturn(department);

        Department result = departmentService.createDepartment(department);
        assertNotNull(result);
        assertEquals("HR", result.getName());

        verify(departmentRepository, times(1)).findByName("HR");
        verify(departmentRepository, times(1)).save(department);
    }

    @Test
    void createDepartment_DuplicateName_ThrowsException() {
        Department department = new Department();
        department.setName("Finance");

        when(departmentRepository.findByName("Finance")).thenReturn(Optional.of(department));

        DepartmentAlreadyExistsException exception = assertThrows(DepartmentAlreadyExistsException.class,
                () -> departmentService.createDepartment(department));
        assertEquals("Department with name 'Finance' already exists.", exception.getMessage());
        verify(departmentRepository, times(1)).findByName("Finance");
        verify(departmentRepository, never()).save(any());
    }

    // Test getAllDepartments() methods
    @Test
    void getAllDepartments_ReturnsList() {
        List<Department> list = Arrays.asList(new Department(), new Department());
        when(departmentRepository.findAll()).thenReturn(list);

        List<Department> result = departmentService.getAllDepartments();
        assertEquals(2, result.size());
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    void getAllDepartments_Pageable_ReturnsPage() {
        Department dept = new Department();
        dept.setName("IT");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Department> page = new PageImpl<>(Collections.singletonList(dept), pageable, 1);
        when(departmentRepository.findAll(pageable)).thenReturn(page);

        Page<Department> result = departmentService.getAllDepartments(pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(departmentRepository, times(1)).findAll(pageable);
    }

    // Test getDepartmentById() method
    @Test
    void getDepartmentById_ExistingId_ReturnsDepartment() {
        UUID id = UUID.randomUUID();
        Department dept = new Department();
        dept.setId(id);
        dept.setName("Marketing");

        when(departmentRepository.findById(id)).thenReturn(Optional.of(dept));

        Department result = departmentService.getDepartmentById(id);
        assertNotNull(result);
        assertEquals("Marketing", result.getName());
        verify(departmentRepository, times(1)).findById(id);
    }

    @Test
    void getDepartmentById_NonExistingId_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(departmentRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> departmentService.getDepartmentById(id));
        assertEquals("Department not found with id: " + id, exception.getMessage());
        verify(departmentRepository, times(1)).findById(id);
    }

    // Test updateDepartment() method
    @Test
    void updateDepartment_SameName_Success() {
        UUID id = UUID.randomUUID();
        Department existing = new Department();
        existing.setId(id);
        existing.setName("Operations");

        Department update = new Department();
        update.setName("Operations");

        when(departmentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(departmentRepository.save(existing)).thenReturn(existing);

        Department result = departmentService.updateDepartment(id, update);
        assertNotNull(result);
        assertEquals("Operations", result.getName());
        verify(departmentRepository, times(1)).findById(id);
        verify(departmentRepository, never()).findByName(any());
        verify(departmentRepository, times(1)).save(existing);
    }

    @Test
    void updateDepartment_ChangedNameUnique_Success() {
        UUID id = UUID.randomUUID();
        Department existing = new Department();
        existing.setId(id);
        existing.setName("OldDept");

        Department update = new Department();
        update.setName("NewDept");

        when(departmentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(departmentRepository.findByName("NewDept")).thenReturn(Optional.empty());
        when(departmentRepository.save(existing)).thenReturn(existing);

        Department result = departmentService.updateDepartment(id, update);
        assertNotNull(result);
        assertEquals("NewDept", result.getName());
        verify(departmentRepository, times(1)).findById(id);
        verify(departmentRepository, times(1)).findByName("NewDept");
        verify(departmentRepository, times(1)).save(existing);
    }

    @Test
    void updateDepartment_ChangedNameDuplicate_ThrowsException() {
        UUID id = UUID.randomUUID();
        Department existing = new Department();
        existing.setId(id);
        existing.setName("OldDept");

        Department update = new Department();
        update.setName("ExistingDept");

        // Simulate that another department exists with the new name.
        when(departmentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(departmentRepository.findByName("ExistingDept")).thenReturn(Optional.of(new Department()));

        DepartmentAlreadyExistsException exception = assertThrows(DepartmentAlreadyExistsException.class,
                () -> departmentService.updateDepartment(id, update));
        assertEquals("Department with name 'ExistingDept' already exists.", exception.getMessage());
        verify(departmentRepository, times(1)).findById(id);
        verify(departmentRepository, times(1)).findByName("ExistingDept");
        verify(departmentRepository, never()).save(any());
    }

    // Test deleteDepartment() method
    @Test
    void deleteDepartment_Success() {
        UUID id = UUID.randomUUID();
        Department existing = new Department();
        existing.setId(id);
        existing.setName("Finance");

        when(departmentRepository.findById(id)).thenReturn(Optional.of(existing));
        doNothing().when(departmentRepository).delete(existing);

        assertDoesNotThrow(() -> departmentService.deleteDepartment(id));
        verify(departmentRepository, times(1)).findById(id);
        verify(departmentRepository, times(1)).delete(existing);
    }

    @Test
    void deleteDepartment_NotFound_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(departmentRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> departmentService.deleteDepartment(id));
        assertEquals("Department not found with id: " + id, exception.getMessage());
        verify(departmentRepository, times(1)).findById(id);
        verify(departmentRepository, never()).delete(any());
    }
}
