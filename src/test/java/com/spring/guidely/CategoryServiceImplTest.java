package com.spring.guidely;

import com.spring.guidely.entities.Category;
import com.spring.guidely.repository.CategoryRepository;
import com.spring.guidely.service.CategoryService;
import com.spring.guidely.service.Impl.CategoryServiceImpl;
import com.spring.guidely.web.error.CategoryAlreadyExistsException;
import com.spring.guidely.web.error.CategoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryService categoryService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        categoryService = new CategoryServiceImpl(categoryRepository);
    }

    // getAllCategories() Tests

    @Test
    void getAllCategories_ReturnsAllCategories() {
        List<Category> categories = new ArrayList<>();
        Category cat1 = new Category();
        cat1.setId(UUID.randomUUID());
        cat1.setName("Cat1");
        cat1.setDescription("Description1");

        Category cat2 = new Category();
        cat2.setId(UUID.randomUUID());
        cat2.setName("Cat2");
        cat2.setDescription("Description2");

        categories.add(cat1);
        categories.add(cat2);

        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findAll();
    }

    // getCategoryById() Tests

    @Test
    void getCategoryById_ExistingId_ReturnsCategory() {
        UUID id = UUID.randomUUID();
        Category category = new Category();
        category.setId(id);
        category.setName("Cat1");
        category.setDescription("Description1");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(id);
        assertNotNull(result);
        assertEquals("Cat1", result.getName());
        verify(categoryRepository, times(1)).findById(id);
    }

    @Test
    void getCategoryById_NonExistingId_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class,
                () -> categoryService.getCategoryById(id));
        assertEquals("Category not found with id: " + id, exception.getMessage());
        verify(categoryRepository, times(1)).findById(id);
    }

    // createCategory() Tests

    @Test
    void createCategory_UniqueName_Success() {
        Category newCategory = new Category();
        newCategory.setName("UniqueCat");
        newCategory.setDescription("Some description");

        when(categoryRepository.findByName("UniqueCat")).thenReturn(Optional.empty());
        when(categoryRepository.save(newCategory)).thenReturn(newCategory);

        Category result = categoryService.createCategory(newCategory);
        assertNotNull(result);
        assertEquals("UniqueCat", result.getName());
        verify(categoryRepository, times(1)).findByName("UniqueCat");
        verify(categoryRepository, times(1)).save(newCategory);
    }

    @Test
    void createCategory_DuplicateName_ThrowsException() {
        Category newCategory = new Category();
        newCategory.setName("DuplicateCat");
        newCategory.setDescription("Some description");

        when(categoryRepository.findByName("DuplicateCat")).thenReturn(Optional.of(newCategory));

        CategoryAlreadyExistsException exception = assertThrows(CategoryAlreadyExistsException.class,
                () -> categoryService.createCategory(newCategory));
        assertEquals("Category already exists with name: DuplicateCat", exception.getMessage());
        verify(categoryRepository, times(1)).findByName("DuplicateCat");
        verify(categoryRepository, never()).save(any());
    }

    // updateCategory() Tests

    @Test
    void updateCategory_SameName_Success() {
        UUID id = UUID.randomUUID();
        Category existingCategory = new Category();
        existingCategory.setId(id);
        existingCategory.setName("Cat1");
        existingCategory.setDescription("Desc1");

        Category updatedCategory = new Category();
        updatedCategory.setName("Cat1");
        updatedCategory.setDescription("New Desc");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);

        Category result = categoryService.updateCategory(id, updatedCategory);
        assertNotNull(result);
        assertEquals("Cat1", result.getName());
        assertEquals("New Desc", result.getDescription());
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).save(existingCategory);
    }

    @Test
    void updateCategory_ChangedNameUnique_Success() {
        UUID id = UUID.randomUUID();
        Category existingCategory = new Category();
        existingCategory.setId(id);
        existingCategory.setName("OldName");
        existingCategory.setDescription("Desc");

        Category updatedCategory = new Category();
        updatedCategory.setName("NewUniqueName");
        updatedCategory.setDescription("New Desc");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findByName("NewUniqueName")).thenReturn(Optional.empty());
        when(categoryRepository.save(existingCategory)).thenReturn(existingCategory);

        Category result = categoryService.updateCategory(id, updatedCategory);
        assertNotNull(result);
        assertEquals("NewUniqueName", result.getName());
        assertEquals("New Desc", result.getDescription());
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).findByName("NewUniqueName");
        verify(categoryRepository, times(1)).save(existingCategory);
    }

    @Test
    void updateCategory_ChangedNameDuplicate_ThrowsException() {
        UUID id = UUID.randomUUID();
        Category existingCategory = new Category();
        existingCategory.setId(id);
        existingCategory.setName("OldName");
        existingCategory.setDescription("Desc");

        Category updatedCategory = new Category();
        updatedCategory.setName("DuplicateName");
        updatedCategory.setDescription("New Desc");

        // Simulate that another category with "DuplicateName" already exists
        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findByName("DuplicateName")).thenReturn(Optional.of(new Category()));

        CategoryAlreadyExistsException exception = assertThrows(CategoryAlreadyExistsException.class,
                () -> categoryService.updateCategory(id, updatedCategory));
        assertEquals("Category already exists with name: DuplicateName", exception.getMessage());
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).findByName("DuplicateName");
        verify(categoryRepository, never()).save(any());
    }

    // deleteCategory() Tests

    @Test
    void deleteCategory_Success() {
        UUID id = UUID.randomUUID();
        Category existingCategory = new Category();
        existingCategory.setId(id);
        existingCategory.setName("Cat1");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        doNothing().when(categoryRepository).delete(existingCategory);

        assertDoesNotThrow(() -> categoryService.deleteCategory(id));
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, times(1)).delete(existingCategory);
    }

    @Test
    void deleteCategory_NotFound_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class,
                () -> categoryService.deleteCategory(id));
        assertEquals("Category not found with id: " + id, exception.getMessage());
        verify(categoryRepository, times(1)).findById(id);
        verify(categoryRepository, never()).delete(any());
    }
}
