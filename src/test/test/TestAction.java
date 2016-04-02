package test.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("action")
public class TestAction {
	@RequestMapping("test")
	public String test(){
		System.out.println("ananana");
		return "index.html";
	}
}
