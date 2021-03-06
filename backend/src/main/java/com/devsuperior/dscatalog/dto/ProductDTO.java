package com.devsuperior.dscatalog.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

public class ProductDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	@Size(min = 3, max = 60, message = "Name must have from 3 to 60 characters")
	@NotBlank(message = "Mandatory field")
	private String name;
	@NotBlank(message = "Mandatory field")
	private String description;
	@Positive(message = "Price must be positive")
	private Double price;
	private String imgUrl;
	@PastOrPresent(message = "Date can't be future")
	private Instant date;

	private List<CategoryDTO> categories = new ArrayList<>();

	public ProductDTO() {	}

	public ProductDTO(Long id, String name, String description, Double price, String imgUrl, Instant date) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.imgUrl = imgUrl;
		this.date = date;
	}

	public ProductDTO(Product obj) {
		this.id = obj.getId();
		this.name = obj.getName();
		this.description = obj.getDescription();
		this.price = obj.getPrice();
		this.imgUrl = obj.getImgUrl();
		this.date = obj.getDate();
	}

	public ProductDTO(Product obj, Set<Category> cats) {
		this(obj); // calls ProductDTO(Product obj)
		cats.forEach(cat -> this.categories.add(new CategoryDTO(cat)));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public Instant getDate() {
		return date;
	}

	public void setDate(Instant date) {
		this.date = date;
	}

	public List<CategoryDTO> getCategories() {
		return categories;
	}

	public void setCategories(List<CategoryDTO> categories) {
		this.categories = categories;
	}

	
	
}
