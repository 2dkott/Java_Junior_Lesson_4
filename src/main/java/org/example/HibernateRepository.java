package org.example;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;

public class HibernateRepository {
    private final SessionFactory sessionFactory;

    public HibernateRepository(String configPath) {
        this.sessionFactory = new Configuration().configure(configPath).buildSessionFactory();
    }

    public void recordBooks(List<Book> books) {
        books.forEach(book -> {
            try(Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                session.persist(book);
                session.getTransaction().commit();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    public void recordAuthors(List<Author> authors) {
        authors.forEach(this::persist);
    }

    private void persist(Object o) {
        try(Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(o);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Book> getBookByAuthor(Author author) {
        try(Session session = sessionFactory.openSession()) {
            CriteriaBuilder sessionCriteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Book> criteriaQuery = sessionCriteriaBuilder.createQuery(Book.class);
            Root<Book> root = criteriaQuery.from(Book.class);
            criteriaQuery.select(root).where(sessionCriteriaBuilder.equal(root.get("author"), author));
            Query<Book> query = session.createQuery(criteriaQuery);
            return query.getResultList();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Author getAuthorById(Long id) {
        try(Session session = sessionFactory.openSession()) {
            return session.get(Author.class, id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<Author> getAllAuthors() {
        try(Session session = sessionFactory.openSession()) {
            CriteriaBuilder sessionCriteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Author> criteriaQuery = sessionCriteriaBuilder.createQuery(Author.class);
            Root<Author> root = criteriaQuery.from(Author.class);
            criteriaQuery.select(root);
            return session.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


}
