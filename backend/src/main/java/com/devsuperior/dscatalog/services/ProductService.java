package com.devsuperior.dscatalog.services;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	@Autowired
	private CategoryRepository categoryrepository;

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Product obj = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Product '" + id + "' not found"));
		return new ProductDTO(obj, obj.getCategories());
	}

//	@Transactional(readOnly = true)
//	public List<ProductDTO> findAll() {
//		List<Product> list = repository.findAll();
////		List<ProductDTO> listDTO = list.stream().map( (obj)-> new ProductDTO(obj) ).collect(Collectors.toList());		
////		return 	listDTO;
//		return list.stream().map(obj -> new ProductDTO(obj)).collect(Collectors.toList());
//	}

	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(Pageable pageable) {
		Page<Product> page = repository.findAll(pageable);
		return page.map(obj -> new ProductDTO(obj));
	}

	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product obj = new Product();
		copyDTOtoEntity(dto, obj);
		obj = repository.save(obj);
		return new ProductDTO(obj);
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		try {
			Product obj = repository.getOne(id);
			copyDTOtoEntity(dto, obj);
			obj = repository.save(obj);
			return new ProductDTO(obj);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(
					"Product '" + id + "' not found");
		}
	}

	// No @Transactional - we need to capture DB exception
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException(
					"Product '" + id + "' not found");
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Database iIntegrity violation");
		}

	}

	/********** Private Methods *********/
	private void copyDTOtoEntity(ProductDTO dto, Product obj) {
		// obj.setId(dto.get); //id is not inserted nor updated
		obj.setName(dto.getName());
		obj.setDescription(dto.getDescription());
		obj.setPrice(dto.getPrice());
		obj.setImgUrl(dto.getImgUrl());
		obj.setDate(dto.getDate());

		obj.getCategories().clear(); // making sure it is empty
		for (CategoryDTO catDTO : dto.getCategories()) {
			Category category = categoryrepository.getOne(catDTO.getId());
			obj.getCategories().add(category);
		}
	}

}
