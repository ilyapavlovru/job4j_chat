package ru.job4j.chat.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.chat.domain.Room;

@Repository
public interface RoomRepository extends CrudRepository<Room, Integer> {
}
