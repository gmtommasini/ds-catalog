package com.devsuperior.dscatalog.services;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Category obj = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category '" + id + "' not found"));
		return new CategoryDTO(obj);
	}
	
//	@Transactional(readOnly = true)
//	public List<CategoryDTO> findAll() {
//		List<Category> list = repository.findAll();
////		List<CategoryDTO> listDTO = list.stream().map( (obj)-> new CategoryDTO(obj) ).collect(Collectors.toList());		
////		return 	listDTO;
//		return list.stream().map(obj -> new CategoryDTO(obj)).collect(Collectors.toList());
//	}
	
	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(PageRequest pageRequest) {
		Page<Category> page = repository.findAll(pageRequest);
		return page.map(obj -> new CategoryDTO(obj));
	}


	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
//		Category obj = new Category();
//		obj.setName( dto.getName() );
//		obj = repository.save(obj);
//		return new CategoryDTO(obj);

		return new CategoryDTO(repository.save(new Category(dto.getName())));
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		try {

			Category obj = repository.getOne(id);
			obj.setName(dto.getName());
			obj = repository.save(obj);
			return new CategoryDTO(obj);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Category '" + id + "' not found");
		}

	}

	// No @Transactional - we need to capture DB exception
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Category '" + id + "' not found");
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Database integrity violation");
		}

	}


}
