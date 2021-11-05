package ru.job4j.chat.service;

import org.springframework.stereotype.Service;
import ru.job4j.chat.domain.Person;
import ru.job4j.chat.domain.Role;
import ru.job4j.chat.repository.PersonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Optional<Person> findPersonById(int id) {
        return personRepository.findById(id);
    }

    public void save(Person person) {
        Role role = new Role();
        role.setId(1);
        person.setRole(role);
        person.setEnabled(true);
        personRepository.save(person);
    }

    public List<Person> findAll() {
        List<Person> rsl = new ArrayList<>();
        personRepository.findAll().forEach(rsl::add);
        return rsl;
    }
}
