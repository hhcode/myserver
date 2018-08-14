package com.huang.myservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String getName() {
		return "spring-home";
	}
}
