package com.encora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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


    // Retrieve last index used.
    @CrossOrigin(origins=allowed_origin)
    @GetMapping("/todos/lastIndex")
    @ResponseStatus(value=HttpStatus.OK)
    public Integer giveMeLastID() {
        return toDosRepository.lastId;
    }


    // Set filters and sorters for our to dos.
    record filtersAndSorters (
        toDoFilters filters,
        SortingsFields sortField,
        SortingOrders sortOrder
    ) {

    }
    @CrossOrigin(origins=allowed_origin)
    @PostMapping("/todos/setFiltSort")
    @ResponseStatus(value=HttpStatus.OK)
    public void setFiltersAndSorters(@RequestBody filtersAndSorters filAndSor) throws Exception {
        // Sorting method.
        Sort sortingMethod = Sort.by(String.valueOf(filAndSor.sortField()));
        if (Objects.equals(String.valueOf(filAndSor.sortOrder()), "DESC")) {
            sortingMethod = sortingMethod.descending();
        }

        // Filter to dos and then sort them.
        toDosRepository.refreshFilteredToDos(
                sortingMethod,
                filAndSor.filters().name(),
                filAndSor.filters().priority(),
                filAndSor.filters().done()
        );
    }


    // Return our todos filtered, sorted AND paginated.
    @CrossOrigin(origins=allowed_origin)
    @GetMapping("/todos/filtSort/{page}")
    @ResponseStatus(value=HttpStatus.OK)
    public List<ToDos> getFilteredToDos(@PathVariable("page") Integer page) {
        if(page <= 0) {
            throw new IllegalArgumentException("invalid page: " + page);
        }

        final int pageSize = 10;
        List<ToDos> myToDos = toDosRepository.getFilteredToDos();

        int fromIndex = (page - 1) * pageSize;
        if (myToDos.size() <= fromIndex) {
            return Collections.emptyList();
        }

        // toIndex exclusive
        return myToDos.subList(fromIndex, Math.min(fromIndex + pageSize, myToDos.size()));
    }


    // Return how many pages after filter and sorting.
    @CrossOrigin(origins=allowed_origin)
    @GetMapping("/todos/filtSort/pages")
    @ResponseStatus(value=HttpStatus.OK)
    public Integer getNumberOfPages() {
        // Number of items divided by page size.
        final int pageSize = 10;
        return (int) Math.ceil((double) toDosRepository.filteredToDos.size() / pageSize);
    }
}
