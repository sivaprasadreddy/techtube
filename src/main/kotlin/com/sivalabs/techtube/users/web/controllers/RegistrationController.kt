package com.sivalabs.techtube.users.web.controllers

import com.sivalabs.techtube.users.domain.RegistrationRequest
import com.sivalabs.techtube.users.domain.UserService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class RegistrationController(
    private val userService: UserService,
) {
    @GetMapping("/register")
    fun registerForm(model: Model): String {
        model["user"] = RegistrationRequest()
        return "registration"
    }

    @PostMapping("/register")
    fun register(
        @ModelAttribute("user") @Valid user: RegistrationRequest,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes,
    ): String {
        if (bindingResult.hasErrors()) {
            return "registration"
        }
        try {
            userService.createUser(user)
            redirectAttributes.addFlashAttribute("message", "Registration successful")
            return "redirect:/login"
        } catch (_: Exception) {
            bindingResult.addError(FieldError("user", "email", "User with email already exists"))
            return "registration"
        }
    }
}
