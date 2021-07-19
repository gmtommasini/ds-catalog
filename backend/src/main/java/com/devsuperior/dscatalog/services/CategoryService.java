package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;
	
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll(){
		List<Category> list = repository.findAll();
//		List<CategoryDTO> listDTO = list.stream().map( (obj)-> new CategoryDTO(obj) ).collect(Collectors.toList());		
//		return 	listDTO;
		return list.stream().map(obj -> new CategoryDTO(obj)).collect(Collectors.toList());	
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Category obj = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category '" + id + "' not found"));
		return new CategoryDTO( obj );
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
//		Category obj = new Category();
//		obj.setName( dto.getName() );
//		obj = repository.save(obj);
//		return new CategoryDTO(obj);
		
		return new CategoryDTO(repository.save(new Category(dto.getName())));
	}
	
//	public Optional<Category> findAll(Long id){
//		return repository.findById(id).orElse(null);	
//	}
}
