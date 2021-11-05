package ru.job4j.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.chat.domain.Person;
import ru.job4j.chat.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class.getSimpleName());

    private final PersonService personService;

    private final BCryptPasswordEncoder encoder;

    private final ObjectMapper objectMapper;

    public UserController(PersonService personService, BCryptPasswordEncoder encoder,
                          ObjectMapper objectMapper) {
        this.personService = personService;
        this.encoder = encoder;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/sign-up")
    public void signUp(@RequestBody Person person) {
        var username = person.getUsername();
        var password = person.getPassword();
        if (username == null || password == null) {
            throw new NullPointerException("Username and password mustn't be empty");
        }
        if (username.length() < 4) {
            throw new IllegalArgumentException("Invalid username. Username length must be more than 3 characters");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Invalid password. Password length must be more than 5 characters");
        }
        person.setPassword(encoder.encode(person.getPassword()));
        personService.save(person);
    }

    @GetMapping("/all")
    public List<Person> findAll() {
        return personService.findAll();
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() {
            {
                put("message", e.getMessage());
                put("type", e.getClass());
            }
        }));
        LOGGER.error(e.getLocalizedMessage());
    }
}
