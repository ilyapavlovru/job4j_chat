package ru.job4j.chat.store;

import org.springframework.stereotype.Component;
import ru.job4j.chat.domain.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserStore {

    private final ConcurrentHashMap<String, Person> users = new ConcurrentHashMap<>();

    public void save(Person person) {
        users.put(person.getUsername(), person);
    }

    public Person findByUsername(String userName) {
        return users.get(userName);
    }

    public List<Person> findAll() {
        return new ArrayList<>(users.values());
    }
}
