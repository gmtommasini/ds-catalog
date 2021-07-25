package com.devsuperior.dscatalog.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {
	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProducts;

	@Autowired
	private ProductRepository repository;

	@BeforeEach
	void setUp() {
		existingId = 1L;
		nonExistingId = 100L;
		countTotalProducts = repository.count();
	}

	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
		Product product = Factory.createProduct();
		product.setId(null);
		product = repository.save(product);
		Assertions.assertEquals(
				countTotalProducts + 1, product.getId(),
				"saveShouldPersistWithAutoIncrementWhenIdIsNull"); // in this case, total count == max id
	}

	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		repository.deleteById(existingId);
		// if object does not exist, an error will be throw -> no need to validate if object exists
		Assertions.assertTrue(
				repository.findById(existingId).isEmpty(),
				"verifing if object was deleted");
		// Assertions.assertFalse( repository.findById(id).isPresent() );
	}

	@Test
	public void deleteShouldThrowExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(
				EmptyResultDataAccessException.class,
				() -> repository.deleteById(nonExistingId),
				"deleteShouldThrowExceptionWhenIdDoesNotExists");
	}

	@Test
	public void findByIdShouldReturnObjectWhenIdExists() {
		Assertions.assertTrue(
				repository.findById(existingId).isPresent(),
				"findByIdShouldReturnObjectWhenIdExists");
	}

	@Test
	public void findByIdShouldReturnEmptyWhenIdDontExists() {
		Assertions.assertTrue(
				repository.findById(nonExistingId).isEmpty(),
				"findByIdShouldReturnEmptyWhenIdDontExists");
	}

}
