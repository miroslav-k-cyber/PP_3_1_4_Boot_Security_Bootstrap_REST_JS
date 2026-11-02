package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;
    
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
    
    // 1. Показать страницу (с поддержкой модалок)
    @GetMapping
    public ModelAndView showAdminPage(@RequestParam(value = "tab", required = false, defaultValue = "users") String tab,
                                      @RequestParam(value = "editId", required = false) Long editId,
                                      @RequestParam(value = "deleteId", required = false) Long deleteId) {
        //@GetMapping
        //public String showAdminPage(@RequestParam(value = "tab", required = false, defaultValue = "users") String tab,
        //                           @RequestParam(value = "editId", required = false) Long editId,
        //                           @RequestParam(value = "deleteId", required = false) Long deleteId,
        //                           Model model) {
        
        ModelAndView mav = new ModelAndView("admin");
        
        // Передча  данных  в коробку mav
        mav.addObject("users", userService.getAllUsers());
        mav.addObject("allRoles", roleService.getAllRoles());
        mav.addObject("newUser", new User());
        mav.addObject("activeTab", tab);
        
        //  String + Model, передача данных в модель
        //    model.addAttribute("users", userService.getAllUsers());
        //    model.addAttribute("allRoles", roleService.getAllRoles());
        //    model.addAttribute("newUser", new User());
        //    model.addAttribute("activeTab", tab);
        
        // Логика для модальных окон, полностью переведенная на addObject
        if (editId != null) {
            mav.addObject("userToEdit", userService.getUserById(editId));
            // model.addAttribute("userToEdit", userService.getUserById(editId));
        }
        if (deleteId != null) {
            mav.addObject("userToDelete", userService.getUserById(deleteId));
            //model.addAttribute("userToDelete", userService.getUserById(deleteId));
        }
        return mav;
        //return "admin";
    }
    
    // 2. Создать пользователя - POST-эндпоинт создания пользователя на ModelAndView
    @PostMapping("/new")
    public ModelAndView createUser(@ModelAttribute("newUser") User user,
                                   @RequestParam(value = "roles", required = false) List<Long> roleIds) {
        userService.saveUser(user, roleIds);
        return new ModelAndView("redirect:/admin");
    }
    
    // Метод обработки POST-запроса для создания нового пользователя с последующим редиректом - эндпоинт создания пользователя
    //@PostMapping("/new")
    //public String createUser(@ModelAttribute("newUser") User user,
    //                         @RequestParam(value = "roles", required = false) List<Long> roleIds) {
    //    userService.saveUser(user, roleIds);
    //    return "redirect:/admin";
    //}
    
    // 3. Обновить пользователя (для модалки Edit)
    @PostMapping("/update")
    public ModelAndView updateUser(@ModelAttribute("userToEdit") User user,
                                   @RequestParam(value = "roles", required = false) List<Long> roleIds) {
        userService.updateUser(user, roleIds);
        return new ModelAndView("redirect:/admin");
    }
    
    // 3. Обновить пользователя (для модалки Edit)
    //@PostMapping("/update")
    //public String updateUser(@ModelAttribute("userToEdit") User user,
    //                         @RequestParam(value = "roles", required = false) List<Long> roleIds) {
    //    userService.updateUser(user, roleIds);
    //    return "redirect:/admin";
    //}
    
    // 4. Удалить пользователя
    @PostMapping("/delete/{id}")
    public ModelAndView deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ModelAndView("redirect:/admin");
    }
    
    // 4. Удалить пользователя
    //@PostMapping("/delete/{id}")
    //public String deleteUser(@PathVariable Long id) {
    //    userService.deleteUser(id);
    //   return "redirect:/admin";
    //}
    
}

