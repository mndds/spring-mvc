package kz.nurimov.accreditation.mvc.controllers;

import kz.nurimov.accreditation.mvc.dto.RegistrationDTO;
import kz.nurimov.accreditation.mvc.dto.UserDTO;
import kz.nurimov.accreditation.mvc.models.User;
import kz.nurimov.accreditation.mvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users/list";
    }
}
