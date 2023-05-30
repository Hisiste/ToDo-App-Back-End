# To Do App (Back End)

A basic API to control and save to dos. **This is just the Back End.**

**_NOTE: This does not have a database, but is made to make the change as
painless as possible. See
[Implementing your own database](#implementing-your-own-database) for more
information._**

## Try it yourself!

Currently the only way of trying the API is by running it locally. **You need to
have [Java 17](https://www.java.com/releases/) and
[Maven](https://maven.apache.org/download.cgi) installed on your computer.**
Follow the steps:

1. Clone this repository.

    ```
    git clone https://github.com/Hisiste/ToDo-App-Back-End.git
    ```

1. Enter the newly created folder.

    ```
    cd ./ToDo-App-Back-End
    ```

1. Run the application.

    ```
    mvn spring-boot:run
    ```

    It'll run on http://localhost:9090/. See
    [application.yml](./src/main/resources/application.yml) to change the port.

## Goals

The program currently has/lacks the following functionality:

-   [x] A GET endpoint (/todos) to list “to do’s”.
    -   **Include pagination.** Pages should be of 10 elements.
-   [x] Sort by priority and/or due date
-   [x] Filter the list of "to do's".
    -   By done or undone.
    -   By the name or part of the name.
    -   By priority.
-   [x] A POST endpoint (/todos) to create “to do’s”
    -   Validations included.
-   [x] A PUT endpoint (/todos/{id}) to update the “to do” name, due date
        and/or priority.
    -   Validations included.
-   [x] A POST endpoint (/todos/{id}/done) to mark “to do” as done
    -   This should update the “done date” property.
    -   If “to do” is already done nothing should happen (no error returned).
-   [x] A PUT endpoint (/todos/{id}/undone) to mark “to do” as undone.
    -   If “to do” is already undone nothing should happen.
    -   If “to do” is done, this should clear the done date.

## How to use

Run the application first. Every to do will have the following information:

-   **Integer** id $\rightarrow$ An ID that defines the to do. Starts at 1.
-   **String** text $\rightarrow$ The name of the to do.
-   **Date** dueDate $\rightarrow$ The date and time the to do is due.
-   **boolean** done $\rightarrow$ If the to do is completed or not.
-   **Date** doneDate $\rightarrow$ When has the to do been completed.
-   **Priority** priority $\rightarrow$ The priority of the to do.

    ```java
    enum Priority {
        Low, Medium, High
    }
    ```

-   **Date** creationDate $\rightarrow$ The date the to do was added.

### API commands

**_TODO._** ~~Heh.~~

## Implementing your own database

To add your database to this project, follow these steps:

1. Add the corresponding dependency to [pom.xml](./pom.xml). _Example using
   Postgresql:_

    ```xml
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    ```

1. Add the following configuration to
   [application.yml](./src/main/resources/application.yml):

    ```yml
    spring:
        datasource:
            url: { your database url }
            username: { your database username }
            password: { your database password }
        jpa:
            hibernate:
                ddl-auto: create-drop
            properties:
                hibernate:
                    dialect: { Your database dialect }
                    format_sql: true
            show-sql: true
    ```

    The `ddl-auto: create-drop` **WILL DESTROY** the schema at the end of the
    session. Be careful and change if necessary.

1. Uncomment code in
   [ToDosRepository.java](./src/main/java/com/encora/ToDosRepository.java).

    ```java
    // Uncomment this for using a database instead.
    public interface ToDosRepository extends JpaRepository<ToDos, Integer>{
        // Get to dos list filtered.
        public List<ToDos> findAllWithFilter(String name, String priority, String done) {
            // Use Queries to filter your to dos.
            return null;
        }
    }
    ```

    You can use [JPA @Query](https://www.baeldung.com/spring-data-jpa-query) to
    help you filter the to dos and finish the function.

1. Comment code in
   [ToDosRepository.java](./src/main/java/com/encora/ToDosRepository.java).

    ```java
    // Comment ALL of this if using a database.
    import org.springframework.data.domain.Example;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;

    // ...

    public class ToDosRepository implements JpaRepository<ToDos, Integer> {

        // ...

        @Override
        public Page<ToDos> findAll(Pageable pageable) {
            return null;
        }
    }
    ```

    This is code used to define our to dos without a database. If you're
    implementing your own database, you don't need this code anymore.

Congratulations! This should be everything you need to do to set up and use your
own database. :)
