package com.devsuperior.dscatalog.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

@DataJpaTest
public class ProductRepositoryTests {
	Long existingId;
	Long nonExistingId;
	
	@Autowired
	private ProductRepository repository;
	
	@BeforeEach
	void setUp() {
		existingId = 1L;
		nonExistingId = 100L;
	}

	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {

		repository.deleteById(existingId);

		// if object does not exist, an error will be throw
		// Assertions.assertFalse( repository.findById(id).isPresent() );
		Assertions.assertTrue(repository.findById(existingId).isEmpty(), "verifing if object was deleted");
	}

	@Test
	public void deleteShouldThrowExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(
				EmptyResultDataAccessException.class, 
				() -> repository.deleteById(nonExistingId),
				"deleteShouldThrowExceptionWhenIdDoesNotExists");
	}

}
