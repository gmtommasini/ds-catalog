package com.devsuperior.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.devsuperior.dscatalog.dto.UserUpdateDTO;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.resources.exceptions.FieldMessage;

public class UserUpdateValidator
		implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {

	@Autowired
	private UserRepository repository;

	@Autowired
	private HttpServletRequest request;

	@Override
	public void initialize(UserUpdateValid ann) {
	}

	@Override
	public boolean isValid(UserUpdateDTO dto,
			ConstraintValidatorContext context) {
		
		@SuppressWarnings(value = "unchecked")
		var uriVars = (Map<String, String>) request
				.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		long userId = Long.parseLong(uriVars.get("id"));

		List<FieldMessage> list = new ArrayList<>();

		/*** Validation tests adds FieldMessages to the list ***/

		// Verifying if email address is already in use
		User user = repository.findByEmail(dto.getEmail());
		if (user != null && user.getId()!=userId) { //email already in use for another user
			list.add(new FieldMessage("email", "Email address already in use"));
		}

		/*
		 * This loop inserts this errors list into the Beans validation list,
		 * the one used in the resources.exceptions handler
		 */
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage())
					.addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty(); // no errors if the list is empty
	}
}
