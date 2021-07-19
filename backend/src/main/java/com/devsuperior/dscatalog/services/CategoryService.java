package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll() {
		List<Category> list = repository.findAll();
//		List<CategoryDTO> listDTO = list.stream().map( (obj)-> new CategoryDTO(obj) ).collect(Collectors.toList());		
//		return 	listDTO;
		return list.stream().map(obj -> new CategoryDTO(obj)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Category obj = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Category '" + id + "' not found"));
		return new CategoryDTO(obj);
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
		}catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Category '" + id + "' not found");
		}
		
	}

}
