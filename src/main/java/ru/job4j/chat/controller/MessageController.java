package ru.job4j.chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.domain.Message;
import ru.job4j.chat.domain.Person;
import ru.job4j.chat.domain.Room;
import ru.job4j.chat.service.MessageService;
import ru.job4j.chat.service.PersonService;
import ru.job4j.chat.service.RoomService;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

@RestController
@RequestMapping("/message")
public class MessageController {

    private final MessageService messageService;

    private final RoomService roomService;

    private final PersonService personService;

    public MessageController(MessageService messageService, RoomService roomService, PersonService personService) {
        this.messageService = messageService;
        this.roomService = roomService;
        this.personService = personService;
    }

    @PostMapping("/room/{roomId}/person/{personId}")
    public ResponseEntity<Message> save(@PathVariable int roomId, @PathVariable int personId,
                                        @Valid @RequestBody Message message) {

        Room room = roomService.findRoomById(roomId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Room is not found. Please, check roomId"
        ));
        Person person = personService.findPersonById(personId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Person is not found. Please, check personId"
        ));

        Message createdMessage = new Message();
        createdMessage.setText(message.getText());
        createdMessage.setPerson(person);
        createdMessage.setRoom(room);

        return new ResponseEntity<Message>(
                messageService.saveMessage(createdMessage),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/room/{roomId}/person/{personId}")
    public Message patch(@PathVariable int roomId, @PathVariable int personId, @Valid @RequestBody Message message )
            throws InvocationTargetException, IllegalAccessException {

        Room room = roomService.findRoomById(roomId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Room is not found. Please, check roomId"
        ));
        Person person = personService.findPersonById(personId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Person is not found. Please, check personId"
        ));

        var current = messageService.findMessageById(message.getId()).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Message is not found. Please, check messageId"
        ));
        message.setPerson(person);
        message.setRoom(room);

        var methods = current.getClass().getDeclaredMethods();
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
                var newValue = getMethod.invoke(message);
                if (newValue != null) {
                    setMethod.invoke(current, newValue);
                }
            }
        }

        messageService.saveMessage(message);
        return current;
    }
}
