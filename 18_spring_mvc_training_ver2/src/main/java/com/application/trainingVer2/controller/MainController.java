package com.application.trainingVer2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping // localhost 요청시 매핑
	public String main() {
		return "redirect:/member/main";
	}
	
}
