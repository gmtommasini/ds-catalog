package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

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
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	private Long existingId;
	private Long referencedId;
	private Long nonExistingId;
	private PageImpl<Product> page; //Concrete type used in tests
	private Product  product;

	@BeforeEach
	void setUp() {
		existingId = 1L;
		nonExistingId = 1000L;
		referencedId = 2L;
		product = Factory.createProduct();
		page = new PageImpl<>(List.of(product));

		/* Configuring Mock behavior */
		Mockito.when(repository.findAll( (Pageable)ArgumentMatchers.any() )).thenReturn(page);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository)
				.deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository)
				.deleteById(referencedId);
	}

	@InjectMocks // repository mock. DO NOT USE @Autowired for mocked test
	private ProductService service;

	@Mock
	private ProductRepository repository;
	
	@Test
	public void findAllPagedShouldReturnPage() {
		Pageable  pageable = PageRequest.of(0, 10);
		Page<ProductDTO> result =  service.findAllPaged(pageable);
		
		Assertions.assertNotNull(result);
		Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
	}

	@Test
	public void deleteShouldReturnNothingWhenIdValid() {
		Assertions.assertDoesNotThrow(() -> service.delete(existingId),
				"deleteShouldReturnNothingWhenIdValid");
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}

	@Test
	public void deleteShouldThrowErrorWhenIdInvalid() {
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> service.delete(nonExistingId),
				"deleteShouldThrowErrorWhenIdInvalid");
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}
	
	@Test
	public void deleteShouldThrowErrorWhenObjIsFK() {
		Assertions.assertThrows(DatabaseException.class,
				() -> service.delete(referencedId),
				"deleteShouldThrowErrorWhenObjIsFK");
		Mockito.verify(repository, Mockito.times(1)).deleteById(referencedId);
	}

}
