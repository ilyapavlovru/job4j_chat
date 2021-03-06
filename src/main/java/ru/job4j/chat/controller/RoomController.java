package ru.job4j.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.domain.Person;
import ru.job4j.chat.domain.Response;
import ru.job4j.chat.domain.Room;
import ru.job4j.chat.service.PersonService;
import ru.job4j.chat.service.RoomService;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;

    private final PersonService personService;

    public RoomController(RoomService roomService, PersonService personService) {
        this.roomService = roomService;
        this.personService = personService;
    }

    @RequestMapping(method= RequestMethod.GET)
    public ResponseEntity<List<Room>> findAll() {
        return new ResponseEntity<>(
                roomService.findAllRooms(),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> findById(@PathVariable int id) {
        Room room = roomService.findRoomById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Room is not found. Please, check roomId"
                ));
        return new ResponseEntity<Room>(
                room,
                HttpStatus.OK
        );
    }

    @PostMapping("/person/{id}")
    public ResponseEntity<Room> save(@Valid @RequestBody Room room, @PathVariable int id) {
        Person person = personService.findPersonById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Person is not found. Please, check personId"
        ));
        room.setPerson(person);
        return new ResponseEntity<Room>(
                roomService.saveRoom(room),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/{roomId}/person/{personId}")
    public Room patch(@PathVariable int roomId, @PathVariable int personId, @Valid @RequestBody Room room)
            throws InvocationTargetException, IllegalAccessException {

        Room foundRoom = roomService.findRoomById(roomId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Room is not found. Please, check roomId"
        ));
        Person foundPerson = personService.findPersonById(personId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Person is not found. Please, check personId"
        ));
        room.setId(roomId);
        room.setPerson(foundPerson);

        var methods = foundRoom.getClass().getDeclaredMethods();
        var namePerMethod = new HashMap<String, Method>();
        for (var method: methods) {
            var name = method.getName();
            if (name.startsWith("get") || name.startsWith("set")) {
                namePerMethod.put(name, method);
            }
        }

        for (var name : namePerMethod.keySet()) {
            if (name.startsWith("get")) {
                var getMethod = namePerMethod.get(name);
                var setMethod = namePerMethod.get(name.replace("get", "set"));
                if (setMethod == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid properties mapping");
                }
                var newValue = getMethod.invoke(room);
                if (newValue != null) {
                    setMethod.invoke(foundRoom, newValue);
                }
            }
        }

        roomService.saveRoom(room);
        return foundRoom;
    }

    @PutMapping("/{roomId}/person/{personId}")
    public ResponseEntity<Room> update(@PathVariable int roomId, @PathVariable int personId, @Valid @RequestBody Room room) {

        Room foundRoom = roomService.findRoomById(roomId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Room is not found. Please, check roomId"
        ));
        Person foundPerson = personService.findPersonById(personId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Person is not found. Please, check personId"
        ));

        foundRoom.setName(room.getName());
        foundRoom.setDescription(room.getDescription());
        foundRoom.setPerson(foundPerson);

        return new ResponseEntity<Room>(
                this.roomService.saveRoom(foundRoom),
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> delete(@PathVariable int id) {
        Room room = new Room();
        room.setId(id);
        roomService.deleteRoom(room);
        return new ResponseEntity<Response>(
                new Response(HttpStatus.OK.value(), "Room has been deleted"),
                HttpStatus.OK);
    }
}
