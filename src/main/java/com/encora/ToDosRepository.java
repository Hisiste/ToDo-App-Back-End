package com.encora;

// Uncomment this for using a database instead.
//public interface ToDosRepository extends JpaRepository<ToDos, Integer>{
//    // Get to dos list filtered.
//    public List<ToDos> findAllWithFilter(String name, String priority, String done) throws Exception {
//        // Use Queries to filter your to dos.
//        return null;
//    }
//}

// Comment ALL of this if using a database.
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ToDosRepository implements JpaRepository<ToDos, Integer> {
    Integer lastId;
    List<ToDos> todos;
    String currentSorting;
    List<ToDos> filteredToDos;
    List<String> currentFilters;

    // Constructor
    public ToDosRepository() {
        this.lastId = 0;
        this.todos = new ArrayList<>();
        this.currentSorting = "id";
        this.filteredToDos = new ArrayList<>();
        this.currentFilters = List.of("", "All", "All");
    }

    // Return all to dos.
    @Override
    public List<ToDos> findAll() {
        return this.todos;
    }

    // Return all to dos and sorted.
    @Override
    public List<ToDos> findAll(Sort sort) {
        List<ToDos> sortedList = new ArrayList<>(this.todos);

        try {
            Comparator<ToDos> comparator = this.getToDoComparator(sort);
            Collections.sort(sortedList, comparator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return sortedList;
    }

    // Save new element.
    @Override
    public <S extends ToDos> S save(S entity) {
        if (entity.getId() != null) {
            // If the entity has an ID, search for it in our list of to dos and
            // replace it.
            ToDos selectedToDo;

            for (int index = 0; index < this.todos.size(); index++) {
                selectedToDo = this.todos.get(index);
                if (Objects.equals(selectedToDo.getId(), entity.getId())) {
                    this.todos.set(index, entity);
                    return null;
                }
            }
        } else {
            // If entity doesn't have an ID, assign it a new one.
            entity.setId(++this.lastId);
        }

        // If the ID couldn't be found or the entity didn't exist, append the
        // entity to our list.
        this.todos.add(entity);

        return null;
    }

    // Retrieve a to do.
    @Override
    public ToDos getById(Integer integer) {
        ToDos selectedToDo;

        for (int index = 0; index < this.todos.size(); index++) {
            selectedToDo = this.todos.get(index);
            if (Objects.equals(selectedToDo.getId(), integer)) {
                return selectedToDo;
            }
        }

        return null;
    }

    // Delete a to do.
    @Override
    public void deleteById(Integer integer) {
        ToDos selectedToDo;

        for (int index = 0; index < this.todos.size(); index++) {
            selectedToDo = this.todos.get(index);
            if (Objects.equals(selectedToDo.getId(), integer)) {
                this.todos.remove(index);
                break;
            }
        }
    }

    // Get to dos list filtered.
    public List<ToDos> findAllWithFilter(String name, String priority, String done) throws Exception {
        List<ToDos> filtered = new ArrayList<>(this.todos);

        if (name != null && !name.equals("")) {
            filtered = filtered.stream()
                    .filter(todo -> todo.getText().contains(name))
                    .collect(Collectors.toList());
        }
        if (priority != null && !priority.equalsIgnoreCase("all")) {
            filtered = filtered.stream()
                    .filter(todo -> Objects.equals(String.valueOf(todo.getPriority()), priority))
                    .collect(Collectors.toList());
        }
        if (done != null && !done.equalsIgnoreCase("all")) {
            switch (done) {
                case "Done":
                    filtered = filtered.stream()
                            .filter(ToDos::isDone)
                            .collect(Collectors.toList());
                    break;

                case "Undone":
                    filtered = filtered.stream()
                            .filter(todo -> !todo.isDone())
                            .collect(Collectors.toList());
                    break;

                default:
                    throw new Exception("Filtering not supported on 'done'.");
            }
        }

        return filtered;
    }

    private Comparator<ToDos> getToDoComparator(Sort sort) throws Exception {
        // Personal function. Creates a `Comparator` based on the `sort`
        // parameter. This is for us to successfully sort our List without the
        // need of a database.
        String sortString = sort.toString();    // <- '{field}: {order}'
        String[] sortCriteria = sortString.split(": ");

        String field = sortCriteria[0];
        String order = sortCriteria[1];

        Comparator<ToDos> comparator = null;
        switch (field) {
            case "Id":
                comparator = Comparator.comparing(ToDos::getId);
                break;

            case "Priority":
                comparator = Comparator.comparing(ToDos::getPriority);
                break;

            case "DueDate":
                comparator = Comparator.comparing(ToDos::getDueDate);
                break;

            default:
                throw new Exception("Field sorting not implemented.");
        }

        if (order.equalsIgnoreCase("desc")) {
            comparator = comparator.reversed();
        }
        return comparator;
    }


    // Filter and then sort all of our to dos.
    public void refreshFilteredToDos(Sort sort, String name, String priority, String done) throws Exception {
        this.filteredToDos = this.findAllWithFilter(name, priority, done);

        try {
            Comparator<ToDos> comparator = this.getToDoComparator(sort);
            Collections.sort(this.filteredToDos, comparator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
    *         N O T   Y E T   D E F I N E D .
    */
    @Override
    public void flush() {

    }

    @Override
    public <S extends ToDos> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends ToDos> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<ToDos> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Integer> integers) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public ToDos getOne(Integer integer) {
        return null;
    }

    @Override
    public ToDos getReferenceById(Integer integer) {
        return null;
    }

    @Override
    public <S extends ToDos> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends ToDos> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends ToDos> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends ToDos> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends ToDos> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends ToDos> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends ToDos, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends ToDos> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<ToDos> findById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Integer integer) {
        return false;
    }

    @Override
    public List<ToDos> findAllById(Iterable<Integer> integers) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(ToDos entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> integers) {

    }

    @Override
    public void deleteAll(Iterable<? extends ToDos> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public Page<ToDos> findAll(Pageable pageable) {
        return null;
    }
}
