package com.encora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    record toDoBody(
            String text,
            Date dueDate,
            Priority priority
    ) {

    }
    @PostMapping("/todos")
    @ResponseStatus(value=HttpStatus.OK)
    public void addToDo(@RequestBody toDoBody toDo) {
        if (toDo.text().length() > 120) {
            throw new longerThanMaxException();
        }
        ToDos todo = new ToDos();
        todo.setText(toDo.text());
        todo.setDueDate(toDo.dueDate());
        todo.setPriority(toDo.priority());
        toDosRepository.save(todo);
    }

    // Updates to do with new information
    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="No to do with such index.")
    public static class toDoNotFound extends RuntimeException {}

    @PutMapping("/todos/{id}")
    @ResponseStatus(value=HttpStatus.OK)
    public void editToDo(@PathVariable("id") Integer id, @RequestBody toDoBody toDo) {
        ToDos selectedToDo = toDosRepository.getById(id);
        if (selectedToDo == null)   throw new toDoNotFound();

        if (toDo.text() != null) {
            if (toDo.text().length() > 120)     throw new longerThanMaxException();
            selectedToDo.setText(toDo.text());
        }
        if (toDo.dueDate() != null)     selectedToDo.setDueDate(toDo.dueDate());
        if (toDo.priority() != null)    selectedToDo.setPriority(toDo.priority());
    }


    // Update a to do with "done".

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


    // Getting sorted to dos.
    enum SortingsFields {
        Id, Priority, DueDate
    }
    enum SortingOrders {
        ASC, DESC
    }
    @GetMapping("/todos/{field}/{order}")
    @ResponseStatus(value=HttpStatus.OK)
    public List<ToDos> getFilteredToDos(@PathVariable("field") SortingsFields field, @PathVariable("order") SortingOrders order) {
        Sort sortingMethod = Sort.by(String.valueOf(field));
        if (Objects.equals(String.valueOf(order), "DESC")) {
            sortingMethod = sortingMethod.descending();
        }

        return toDosRepository.findAll(sortingMethod);
    }
}
