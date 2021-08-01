package com.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class) // @WebMvcTest loads only the web layer
public class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ProductService service;

	@Autowired
	private ObjectMapper objectMapper;

	private ProductDTO productDTO;
	private PageImpl<ProductDTO> page;
	private Long validId;
	private Long invalidId;
	private Long referencedId;

	@BeforeEach
	void setUp() throws Exception {

		productDTO = Factory.createProductDTO();
		page = new PageImpl<>(List.of(productDTO));
		validId = productDTO.getId();
		invalidId = 1000L;
		referencedId = 2L;

		/* Configuring Mock behavior */
		Mockito.when(service.findAllPaged(ArgumentMatchers.any()))
				.thenReturn(page);
		Mockito.when(service.findById(validId)).thenReturn(productDTO);
		Mockito.when(service.findById(invalidId))
				.thenThrow(ResourceNotFoundException.class);
		Mockito.when(service.update(ArgumentMatchers.eq(validId),
				ArgumentMatchers.any())).thenReturn(productDTO);
		Mockito.when(service.update(ArgumentMatchers.eq(invalidId),
				ArgumentMatchers.any()))
				.thenThrow(ResourceNotFoundException.class);
		Mockito.doNothing().when(service).delete(validId);
		Mockito.doThrow(ResourceNotFoundException.class).when(service)
				.delete(invalidId);
		Mockito.doThrow(DatabaseException.class).when(service)
				.delete(referencedId);
		Mockito.when( service.insert(ArgumentMatchers.any()) ).thenReturn(productDTO);
	}


	@Test
	public void deleteShouldReturnNotFoundWhenInvalidID() throws Exception{
		ResultActions result = mockMvc.perform(delete("/products/{id}", invalidId));
		result.andExpect(status().isNotFound());
	}
	@Test
	public void deleteShouldReturnNoContentWhenValidID() throws Exception{
		ResultActions result = mockMvc.perform(delete("/products/{id}", validId));
		result.andExpect(status().isNoContent());
	}	
	
	@Test
	public void insertShouldReturnProductDTOandCreatedStatus() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		ResultActions result = mockMvc.perform(post("/products")
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists()); // $ is the root of the result in this case
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenValidID() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		ResultActions result = mockMvc.perform(put("/products/{id}", validId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists()); // $ is the root of the result in this case
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
		// ... more assertions
	}

	@Test
	public void updateShouldReturnNotFoundWhenInvalidID() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		ResultActions result = mockMvc.perform(put("/products/{id}", invalidId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		result.andExpect(status().isNotFound());
	}

	@Test
	public void findByIdShouldReturnProductDTOWhenValidID() throws Exception {
		ResultActions result = mockMvc.perform(get("/products/{id}", validId)
				.accept(MediaType.APPLICATION_JSON));
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists()); // $ is the root of the result in this case
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
		// mockMvc.perform(get("/products")).andExpect(status().isOk());// get on /products path and expects a 200 code
	}

	@Test
	public void findByIdShouldReturnNotFoundWhenInvalidID() throws Exception {
		ResultActions result = mockMvc.perform(get("/products/{id}", invalidId)
		// .accept(MediaType.APPLICATION_JSON)
		);
		result.andExpect(status().isNotFound());
	}

	@Test
	public void findAllShouldReturnPage() throws Exception {
		ResultActions result = mockMvc.perform(get("/products")
				.accept(MediaType.APPLICATION_JSON));
		result.andExpect(status().isOk());
		// mockMvc.perform(get("/products")).andExpect(status().isOk());// get on /products path and expects a 200 code

	}

}
