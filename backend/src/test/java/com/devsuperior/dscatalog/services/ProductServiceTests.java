package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	private Long validId;
	private Long referencedId;
	private Long invalidId;
	private PageImpl<Product> page; // Concrete type used in tests
	private Product product;
	private ProductDTO productDTO;
	private Category category;
	@InjectMocks // repository mock. DO NOT USE @Autowired for mocked test
	private ProductService service;

	@Mock
	private ProductRepository repository;
	@Mock
	private CategoryRepository categoryRepository;

	@BeforeEach
	void setUp() throws Exception {
		product = Factory.createProduct();
		validId = product.getId();
		invalidId = 1000L;
		referencedId = 2L;
		page = new PageImpl<>(List.of(product));
		category = new Category(2L, "Test Category");
		productDTO = Factory.createProductDTO();

		/* Configuring Mock behavior */
		// insert and update
		Mockito.when(repository.save(ArgumentMatchers.any()))
				.thenReturn(product);

		// getOne
		Mockito.when(repository.getOne(validId))
				.thenReturn(product);
		Mockito.when(repository.getOne(invalidId))
				.thenThrow(EntityNotFoundException.class);
		Mockito.when(categoryRepository.getOne(validId))
				.thenReturn(category);

		// Find
		Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any()))
				.thenReturn(page);
		Mockito.when(repository.findById(validId))
				.thenReturn(Optional.of(product));
		Mockito.when(repository.findById(invalidId))
				.thenReturn(Optional.empty());

		// Delete
		Mockito.doNothing().when(repository).deleteById(validId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository)
				.deleteById(invalidId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository)
				.deleteById(referencedId);
	}

	// Find tests *************************************
	@Test
	public void findByIdShouldReturnProductDTOWhenIdValid() {
		ProductDTO result = service.findById(validId);
		Assertions.assertNotNull(result);
		Mockito.verify(repository, Mockito.times(1)).findById(validId);
	}

	@Test
	public void findByIdShouldThrowErrorWhenIdInvalid() {
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> service.findById(invalidId),
				"findByIdShouldThrowErrorWhenIdInvalid");
		Mockito.verify(repository, Mockito.times(1)).findById(invalidId);
	}

	@Test
	public void findAllPagedShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<ProductDTO> result = service.findAllPaged(pageable);

		Assertions.assertNotNull(result);
		Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
	}

	// Update tests *************************************
	@Test
	public void updateShouldUpdateObjectWhenIdValid() {
		ProductDTO initial = Factory.createProductDTO();		// ProductDTO(validId, "Test", "Test objec", 0.0, "url", null);
		ProductDTO result = service.update(validId, initial);
		ProductDTO expected = new ProductDTO(product);
		//Assertions.assertTrue(result.equals(expected));
		Assertions.assertTrue(result.getCategories().equals(expected.getCategories()), "categories");
		Assertions.assertTrue(result.getDate().equals(expected.getDate()), "dates");
		Assertions.assertTrue(result.getDescription().equals(expected.getDescription()), "description");
		Assertions.assertTrue(result.getId().equals(expected.getId()),"ID");
		Assertions.assertTrue(result.getImgUrl().equals(expected.getImgUrl()), "imgURL");
		Assertions.assertTrue(result.getName().equals(expected.getName()), "name");
		Assertions.assertTrue(result.getPrice().equals(expected.getPrice()),"price");
		
		Mockito.verify(repository, Mockito.times(1)).getOne(validId);
		Mockito.verify(categoryRepository, Mockito.times(1)).getOne(2L);
		Mockito.verify(repository, Mockito.times(1)).save(product);
	}
	@Test
	public void updateShouldThrowErrorWhenIdInvalid() {
		Assertions.assertThrows(ResourceNotFoundException.class,
				()->service.update(invalidId, productDTO),
				"updateShouldThrowErrorWhenIdInvalid");
		Mockito.verify(repository, Mockito.times(1)).getOne(invalidId);
	}

	
	// Delete tests *************************************
	@Test
	public void deleteShouldReturnNothingWhenIdValid() {
		Assertions.assertDoesNotThrow(() -> service.delete(validId),
				"deleteShouldReturnNothingWhenIdValid");
		Mockito.verify(repository, Mockito.times(1)).deleteById(validId);
	}

	@Test
	public void deleteShouldThrowErrorWhenIdInvalid() {
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> service.delete(invalidId),
				"deleteShouldThrowErrorWhenIdInvalid");
		Mockito.verify(repository, Mockito.times(1)).deleteById(invalidId);
	}

	@Test
	public void deleteShouldThrowErrorWhenObjIsFK() {
		Assertions.assertThrows(DatabaseException.class,
				() -> service.delete(referencedId),
				"deleteShouldThrowErrorWhenObjIsFK");
		Mockito.verify(repository, Mockito.times(1)).deleteById(referencedId);
	}

}
