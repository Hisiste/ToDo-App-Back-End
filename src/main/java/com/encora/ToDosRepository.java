package com.encora;

// Uncomment this for using a database instead.
//public interface ToDosRepository extends JpaRepository<ToDos, Integer>{
//}

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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

    // Save new element.
    @Override
    public <S extends ToDos> S save(S entity) {
        entity.setId(++this.lastId);
        this.todos.add(entity);
        return null;
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
    public ToDos getById(Integer integer) {
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
    public void deleteById(Integer integer) {

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
    public List<ToDos> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<ToDos> findAll(Pageable pageable) {
        return null;
    }
}
