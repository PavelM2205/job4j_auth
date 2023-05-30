package ru.job4j.auth.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.job4j.auth.model.Person;
import ru.job4j.auth.repository.PersonRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final PersonRepository personRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person user = this.findByLogin(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(user.getLogin(), user.getPassword(), Collections.emptyList());
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Optional<Person> findById(int id) {
        return personRepository.findById(id);
    }

    public Person findByLogin(String login) {
        return personRepository.findByLogin(login);
    }

    public Person create(Person person) {
        return personRepository.save(person);
    }

    public boolean update(Person person) {
        boolean result = false;
        if (personRepository.findById(person.getId()).isPresent()) {
            personRepository.save(person);
            result = true;
        }
        return result;
    }

    public boolean delete(int id) {
        boolean result = false;
        if (personRepository.findById(id).isPresent()) {
            Person person = new Person();
            person.setId(id);
            personRepository.delete(person);
            result = true;
        }
        return result;
    }
}
