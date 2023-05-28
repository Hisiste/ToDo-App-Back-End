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
    // Edit this origin and set where the Front End is allocated.
    private static final String allowed_origin = "http://localhost:8080/";


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
    @CrossOrigin(origins=allowed_origin)
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
    @CrossOrigin(origins=allowed_origin)
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

    @CrossOrigin(origins=allowed_origin)
    @PutMapping("/todos/{id}")
    @ResponseStatus(value=HttpStatus.OK)
    public void editToDo(@PathVariable("id") Integer id, @RequestBody toDoBody toDo) {
        ToDos selectedToDo = toDosRepository.getById(id);
        if (selectedToDo == null)   throw new toDoNotFound();

        if (toDo.text() != null) {
            if (toDo.text().length() > 120)     throw new longerThanMaxException();
            selectedToDo.setText(toDo.text());
        }
        if (toDo.dueDate() != null) {
            if (toDo.dueDate().equals(new Date(0))) {
                selectedToDo.setDueDate(null);
            } else {
                selectedToDo.setDueDate(toDo.dueDate());
            }
        }
        if (toDo.priority() != null)    selectedToDo.setPriority(toDo.priority());
    }


    // Deletes a to do by index.
    @CrossOrigin(origins=allowed_origin)
    @DeleteMapping("/todos/{id}")
    @ResponseStatus(value=HttpStatus.OK)
    public void removeToDo(@PathVariable("id") Integer id) {
        toDosRepository.deleteById(id);
    }


    // Update a to do with "done".
    @CrossOrigin(origins=allowed_origin)
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
    @CrossOrigin(origins=allowed_origin)
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
    @CrossOrigin(origins=allowed_origin)
    @GetMapping("/todos/{field}/{order}")
    @ResponseStatus(value=HttpStatus.OK)
    public List<ToDos> getSortedToDos(@PathVariable("field") SortingsFields field, @PathVariable("order") SortingOrders order) {
        Sort sortingMethod = Sort.by(String.valueOf(field));
        if (Objects.equals(String.valueOf(order), "DESC")) {
            sortingMethod = sortingMethod.descending();
        }

        return toDosRepository.findAll(sortingMethod);
    }


    // Getting filtered to dos.
    record toDoFilters (
            String name,
            String priority,
            String done
    ) {

    }
    @CrossOrigin(origins=allowed_origin)
    @GetMapping("/todos/filter")
    @ResponseStatus(value=HttpStatus.OK)
    public List<ToDos> getFilteredToDos(@RequestBody toDoFilters filters) throws Exception {
        return toDosRepository.findAllWithFilter(filters.name(), filters.priority(), filters.done());
    }
}
