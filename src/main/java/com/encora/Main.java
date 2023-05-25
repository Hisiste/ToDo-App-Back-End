package com.encora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("v1")
public class Main {
    private final ToDosRepository toDosRepository;

    public Main(ToDosRepository toDosRepository) {
        this.toDosRepository = toDosRepository;
    }
    public Main() {
        this.toDosRepository = new ToDosRepository();
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @GetMapping("/todos")
    public List<ToDos> getToDos() {
        return toDosRepository.findAll();
    }

    record NewToDo(
            String text,
            Date dueDate,
            Priority priority
    ) {

    }
    @PostMapping
    public void addToDo(@RequestBody NewToDo toDo) {
        ToDos todo = new ToDos();
        todo.setText(toDo.text());
        todo.setDueDate(toDo.dueDate());
        todo.setPriority(toDo.priority());
        toDosRepository.save(todo);
    }
}
