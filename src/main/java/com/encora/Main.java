package com.encora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
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

    // Get all to dos.
    @GetMapping("/todos")
    @ResponseStatus(value=HttpStatus.OK)
    public List<ToDos> getToDos() {
        return toDosRepository.findAll();
    }

    // Add a new to do.
    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Text is longer than 120 characters.")
    public static class longerThanMaxException extends RuntimeException {}
    record NewToDo(
            String text,
            Date dueDate,
            Priority priority
    ) {

    }
    @PostMapping("/todos")
    @ResponseStatus(value=HttpStatus.OK)
    public void addToDo(@RequestBody NewToDo toDo) {
        if (toDo.text().length() > 120) {
            throw new longerThanMaxException();
        }
        ToDos todo = new ToDos();
        todo.setText(toDo.text());
        todo.setDueDate(toDo.dueDate());
        todo.setPriority(toDo.priority());
        toDosRepository.save(todo);
    }

    // Update a to do with "done".
    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="No to do with such index.")
    public static class toDoNotFound extends RuntimeException {}

    @PostMapping("/todos/{id}/done")
    @ResponseStatus(value=HttpStatus.OK)
    public void setDone(@PathVariable("id") Integer id) {
        ToDos selectedToDo = toDosRepository.getById(id);
        if (selectedToDo == null)   throw new toDoNotFound();
        if (selectedToDo.isDone())  return;

        selectedToDo.setDone(true);
        selectedToDo.setDoneDate(new Date());
        toDosRepository.save(selectedToDo);
    }

    // Update a to do to set "done" as false.
    @PutMapping("/todos/{id}/undone")
    @ResponseStatus(value=HttpStatus.OK)
    public void setUndone(@PathVariable("id") Integer id) {
        ToDos selectedToDo = toDosRepository.getById(id);
        if (selectedToDo == null)   throw new toDoNotFound();
        if (!selectedToDo.isDone())  return;

        selectedToDo.setDone(false);
        selectedToDo.setDoneDate(null);
        toDosRepository.save(selectedToDo);
    }
}
