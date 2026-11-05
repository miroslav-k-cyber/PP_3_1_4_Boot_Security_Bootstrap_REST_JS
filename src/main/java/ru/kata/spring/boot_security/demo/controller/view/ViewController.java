package ru.kata.spring.boot_security.demo.controller.view;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ViewController {
    
    // Страница админа (отдает только HTML, данные подгрузит JS)
    @GetMapping("/admin")
    public ModelAndView adminPage() {
        return new ModelAndView("admin");
    }
    
    // Страница пользователя (отдает только HTML, данные подгрузит JS)
    @GetMapping("/user")
    public ModelAndView userPage() {
        return new ModelAndView("user");
    }
    
    // Редирект с корня на /admin
    @GetMapping("/")
    public String home() {
        return "redirect:/admin";
    }
}
