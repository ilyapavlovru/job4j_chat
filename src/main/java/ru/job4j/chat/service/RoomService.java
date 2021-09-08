package ru.job4j.chat.service;

import org.springframework.stereotype.Service;
import ru.job4j.chat.domain.Room;
import ru.job4j.chat.repository.RoomRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> findAllRooms() {
        List<Room> rsl = new ArrayList<>();
        roomRepository.findAll().forEach(rsl::add);
        return rsl;
    }
}
