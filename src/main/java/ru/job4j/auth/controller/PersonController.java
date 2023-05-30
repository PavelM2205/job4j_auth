package ru.job4j.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.service.UserDetailsServiceImpl;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/person")
@AllArgsConstructor
public class PersonController {
    private static final Logger LOG = LoggerFactory.getLogger(PersonController.class);
    private final ObjectMapper objectMapper;
    private final UserDetailsServiceImpl persons;
    private final BCryptPasswordEncoder encoder;

    @GetMapping("/all")
    public List<Person> findAll() {
        return persons.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        if (id == 0) {
            throw new NullPointerException("Id mustn't be zero");
        }
        var person = persons.findById(id);
        if (person.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Person is not found");
        }
        return new ResponseEntity<>(person.get(), HttpStatus.OK);
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        if (person.getLogin().isEmpty() || person.getPassword().isEmpty()) {
            throw new NullPointerException("Login or password mustn't be empty");
        }
        if (!persons.update(person)) {
            throw new IllegalStateException("Person failed to update");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (id == 0) {
            throw new NullPointerException("Id mustn't be zero");
        }
        if (!persons.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Person is not found");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@RequestBody Person person) {
        if (person.getLogin().isEmpty() || person.getPassword().isEmpty()) {
            throw new NullPointerException("Login or password mustn't be empty");
        }
        person.setPassword(encoder.encode(person.getPassword()));
        persons.create(person);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ExceptionHandler(value = {IllegalStateException.class})
    public void exceptionHandler(Exception exc, HttpServletResponse res)
            throws IOException {
        res.setStatus(HttpStatus.BAD_REQUEST.value());
        res.setContentType("application/json");
        res.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() {{
            put("message", exc.getMessage());
            put("type", exc.getClass());
        }}));
        LOG.error(exc.getMessage());
    }
}
