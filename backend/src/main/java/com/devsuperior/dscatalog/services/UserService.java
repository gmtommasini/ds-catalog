package com.devsuperior.dscatalog.services;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.RoleDTO;
import com.devsuperior.dscatalog.dto.UserDTO;
import com.devsuperior.dscatalog.dto.UserInsertDTO;
import com.devsuperior.dscatalog.dto.UserUpdateDTO;
import com.devsuperior.dscatalog.entities.Role;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.RoleRepository;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class UserService implements UserDetailsService {
	
	private static Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private BCryptPasswordEncoder passwordEncoder; // AppConfig.java

	@Autowired
	private UserRepository repository;
	@Autowired
	private RoleRepository roleRepository;

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		User obj = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(
						"User '" + id + "' not found"));
		return new UserDTO(obj);
	}

//	@Transactional(readOnly = true)
//	public List<UserDTO> findAll() {
//		List<User> list = repository.findAll();
////		List<UserDTO> listDTO = list.stream().map( (obj)-> new UserDTO(obj) ).collect(Collectors.toList());		
////		return 	listDTO;
//		return list.stream().map(obj -> new UserDTO(obj)).collect(Collectors.toList());
//	}

	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable) {
		Page<User> page = repository.findAll(pageable);
		return page.map(obj -> new UserDTO(obj));
	}

	@Transactional
	public UserDTO insert(UserInsertDTO dto) {
		try {
			User obj = new User();
			copyDTOtoEntity(dto, obj);
			obj.setPassword(passwordEncoder.encode(dto.getPassword())); // encrypting password
			obj = repository.save(obj);
			return new UserDTO(obj);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(
					e.getMessage());
		}
	}

	@Transactional
	public UserDTO update(Long id, UserUpdateDTO dto) {
		try {
			User obj = repository.getOne(id);
			copyDTOtoEntity(dto, obj);
			obj = repository.save(obj);
			return new UserDTO(obj);
		} catch (EntityNotFoundException e) { // TO DO -- error when ROLE doesnt exist shown as user
			throw new ResourceNotFoundException(
					e.getMessage());
		}
		// catch (Exception e) {
		// TODO: handle exception
		// }
	}

	// No @Transactional - we need to capture DB exception
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException(
					"User '" + id + "' not found");
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Database iIntegrity violation");
		}

	}

	@Override
	public UserDetails loadUserByUsername(String username) // username is the email in this case
			throws UsernameNotFoundException {
		User user = repository.findByEmail(username);
		if(user == null) {
			logger.error("User not found: " + username);
			throw new UsernameNotFoundException("User/email not found");
		}
		logger.info("User found: " + username);
		return user; //User implements UserDetail
	}

	/********** Private Methods *********/
	private void copyDTOtoEntity(UserDTO dto, User obj) {
		// obj.setId(dto.get); //id is not inserted nor updated
		obj.setFirstName(dto.getFirstName());
		obj.setLastName(dto.getLastName());
		obj.setEmail(dto.getEmail());
		// password?

		obj.getRoles().clear(); // making sure it is empty
		for (RoleDTO roleDTO : dto.getRoles()) {
			Role role = roleRepository.getOne(roleDTO.getId());
			obj.getRoles().add(role);
		}
	}

}
