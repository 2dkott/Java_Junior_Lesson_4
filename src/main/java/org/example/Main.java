package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {

    static List<Author> authors = prepareAuthors(5);

    public static void main(String[] args) {
        runJdbcTask();
        //runHibernateTask();
    }
    public static void runHibernateTask() {
        HibernateRepository hibernateRepository = new HibernateRepository("hibernate.cfg.xml");
        hibernateRepository.recordAuthors(authors);
        List<Author> loadedAuthors = hibernateRepository.getAllAuthors();
        List<Book> books = prepareBooks(loadedAuthors, 10);
        hibernateRepository.recordBooks(books);
        Author author = hibernateRepository.getAuthorById(1L);
        List<Book> loadedBooks = hibernateRepository.getBookByAuthor(author);
        System.out.println(loadedBooks);

    }

    public static void runJdbcTask() {
        try (JDBCRepository jdbcRepository = new JDBCRepository("jdbc:h2:mem:database")) {
            List<Book> books = prepareBooks(authors, 10);
            jdbcRepository.runExecute("CREATE TABLE books (" +
                    "id bigint," +
                    "name varchar(255)," +
                    "author varchar(255)" +
                    ")");

            books.forEach(book -> {
                try {
                    jdbcRepository.runUpdate(String.format("INSERT INTO books VALUES (%s, '%s', '%s')", book.getId(), book.getName(), book.getAuthor().getName()));

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            ResultSet resultSet = jdbcRepository.runQuery("select * from books where author like '%TestAuthor%'");
            System.out.println(resultSet);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<Book> prepareBooks(List<Author> authors, int amount) {
        List<Book> tempList = new ArrayList<>();
        AuthorRetrieval authorRetrieval = new AuthorRetrieval(authors);
        for(int i = 0; i < amount; i++) {
            Book newBook = new Book();
            newBook.setName(String.format("Test%s", i));
            newBook.setAuthor(authorRetrieval.next());
            tempList.add(newBook);
        }
        return tempList;
    }

    public static List<Author> prepareAuthors(int amount) {
        List<Author> tempList = new ArrayList<>();
        for(int i = 0; i < amount; i++) {
            Author newAuthor = new Author();
            newAuthor.setName(String.format("TestAuthor%s", i));
            tempList.add(newAuthor);
        }
        return tempList;
    }

    static class AuthorRetrieval {
        List<Author> authors;
        Iterator<Author> iterator;

        public AuthorRetrieval(List<Author> authors) {
            this.authors = authors;
            this.iterator = authors.iterator();
        }

        public Author next() {
            if (!iterator.hasNext())
                iterator = authors.iterator();
            return iterator.next();
        }
    }


}