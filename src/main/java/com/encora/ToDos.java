package com.encora;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

enum Priority {
    Low, Medium, High
}

@Entity
public class ToDos {
    @Id
    @SequenceGenerator(
            name = "to_do_id",
            sequenceName = "to_do_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "to_do_id"
    )
    private Integer id;
    private String text;
    private Date dueDate;
    private boolean done;
    private Date doneDate;
    private Priority priority;
    private Date creationDate;

    public ToDos(String text, Date dueDate, Priority priority) {
        this.text = text;
        this.dueDate = dueDate;
        this.priority = priority;
        this.creationDate = new Date();
    }

    public ToDos() {
        this.creationDate = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Date getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(Date doneDate) {
        this.doneDate = doneDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToDos toDos = (ToDos) o;
        return done == toDos.done && Objects.equals(id, toDos.id) && Objects.equals(text, toDos.text) && Objects.equals(dueDate, toDos.dueDate) && Objects.equals(doneDate, toDos.doneDate) && priority == toDos.priority && Objects.equals(creationDate, toDos.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, dueDate, done, doneDate, priority, creationDate);
    }

    @Override
    public String toString() {
        return "ToDos{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", dueDate=" + dueDate +
                ", done=" + done +
                ", doneDate=" + doneDate +
                ", priority=" + priority +
                ", creationDate=" + creationDate +
                '}';
    }
}
