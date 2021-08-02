package com.devsuperior.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional //Independent tests -> tests cannot depend on each other
public class ProductServiceIntegratedTests {
	private Long validId;
	private Long invalidId;
	private Long countTotalProducts;
	// private Long referencedId;

	@Autowired
	private ProductService service;
	@Autowired
	private ProductRepository repository;

	@BeforeEach
	void setUp() throws Exception {
		validId = 1L;
		invalidId = 1000L;
		countTotalProducts = 25L;

	}
	
	@Test
	public void findAllPagedShouldReturnOrderedPageWhenSorted() {
		PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
		Page<ProductDTO> result = service.findAllPaged(pageRequest);

		Assertions.assertFalse(result.isEmpty(), "testing if something came");
		Assertions.assertEquals(0, result.getNumber(), "checking if page number is zero");
		Assertions.assertEquals(10, result.getSize(), "checking page size");
		Assertions.assertEquals(countTotalProducts, result.getTotalElements(), "checking total elements field");
		
		Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName(),"check first element");
		Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName(),"check second element");
		Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName(),"check third element");
	}
	
	@Test
	public void findAllPagedShouldReturnEmptyPageWhenPageDoesntExist() {
		PageRequest pageRequest = PageRequest.of(100, 10);
		Page<ProductDTO> result = service.findAllPaged(pageRequest);

		Assertions.assertTrue(result.isEmpty(), "testing if something came");
		Assertions.assertEquals(100, result.getNumber(), "checking if page number is zero");
		Assertions.assertEquals(10, result.getSize(), "checking page size");
		Assertions.assertEquals(countTotalProducts, result.getTotalElements(), "checking total elements field");
	}
	
	@Test
	public void findAllPagedShouldReturnPage() {
		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<ProductDTO> result = service.findAllPaged(pageRequest);

		Assertions.assertFalse(result.isEmpty(), "testing if something came");
		Assertions.assertEquals(0, result.getNumber(), "checking if page number is zero");
		Assertions.assertEquals(10, result.getSize(), "checking page size");
		Assertions.assertEquals(countTotalProducts, result.getTotalElements(), "checking total elements field");
	}

	@Test
	public void deleteShouldDeleteResourceWhenValidID() {
		service.delete(validId);
		Assertions.assertEquals(countTotalProducts - 1, repository.count(),
				"deleteShouldDeleteResourceWhenValidID");
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenInalidID() {

		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> service.delete(invalidId),
				"deleteShouldThrowResourceNotFoundExceptionWhenInalidID");

	}
}
