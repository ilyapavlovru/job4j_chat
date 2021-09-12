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
    public ResponseEntity<Message> save(@PathVariable int roomId, @PathVariable int personId, @RequestBody Message message) {

        var messageText = message.getText();
        if (messageText == null) {
            throw new NullPointerException("Message text mustn't be empty");
        }

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
}
