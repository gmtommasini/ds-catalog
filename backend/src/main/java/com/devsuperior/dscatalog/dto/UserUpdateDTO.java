package com.devsuperior.dscatalog.dto;

import com.devsuperior.dscatalog.services.validation.UserUpdateValid;

@UserUpdateValid
public class UserUpdateDTO extends UserDTO {
	private static final long serialVersionUID = 1L;
	/*
	 * This class has the same attribute as UserDTO.
	 * This class is used for validation when updating User data
	 */

}
