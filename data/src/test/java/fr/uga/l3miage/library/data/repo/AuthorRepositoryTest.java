package fr.uga.l3miage.library.data.repo;

import fr.uga.l3miage.library.data.domain.Author;
import fr.uga.l3miage.library.data.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class AuthorRepositoryTest extends Base {

    @Autowired
    AuthorRepository authorRepository;

    @Test
    void all(){
        Author a1 = Fixtures.newAuthor();
        a1.setFullName("William Gibson");
        Author a2 = Fixtures.newAuthor();
        a2.setFullName("Victor Hugo");
        Author a3 = Fixtures.newAuthor();
        a3.setFullName("Moliere");
        Author a4 = Fixtures.newAuthor();
        a4.setFullName("Honore de Balzac");
        Author a5 = Fixtures.newAuthor();
        a5.setFullName("Emile Zola");
        Author a6 = Fixtures.newAuthor();
        a6.setFullName("Marie de France");
        Author a7 = Fixtures.newAuthor();
        a7.setFullName("Françoise Sagan");



    }

    @Test
    void searchByName() {

        Author a1 = Fixtures.newAuthor();
        a1.setFullName("William Gibson");
        Author a2 = Fixtures.newAuthor();
        a2.setFullName("Arthur Hemingway");
        entityManager.persist(a1);
        entityManager.persist(a2);
        entityManager.flush();
        entityManager.detach(a1);
        entityManager.detach(a2);

        List<Author> authors = authorRepository.searchByName("Will");
        assertThat(authors).containsExactly(a1);

        /**
         * TESTS SUPPLEMENTAIRES
         */
        List<Author> authors1 = authorRepository.searchByName("thur");
        assertThat(authors1).containsExactly(a2);

        List<Author> authors2 = authorRepository.searchByName("Gibson");
        assertThat(authors2).containsExactly(a1);

        List<Author> authors3 = authorRepository.searchByName("William Gibson");
        assertThat(authors3).containsExactly(a1);

        List<Author> authors4 = authorRepository.searchByName("Arth");
        assertThat(authors4).containsExactly(a2);

        List<Author> authors5 = authorRepository.searchByName("Arthur Hemingway");
        assertThat(authors5).containsExactly(a2);

    }

    @Test
    void findAuthorByIdHavingCoAuthoredBooks() {

        Author a1 = Fixtures.newAuthor();
        Author a2 = Fixtures.newAuthor();
        Author a3 = Fixtures.newAuthor();
        Book b1 = Fixtures.newBook();
        Book b2 = Fixtures.newBook();
        a1.addBook(b1);
        a2.addBook(b2);
        a3.addBook(b2);
        entityManager.persist(a1);
        entityManager.persist(a2);
        entityManager.persist(a3);
        b1.addAuthor(a1);
        b2.addAuthor(a2);
        b2.addAuthor(a3);
        entityManager.persist(b1);
        entityManager.persist(b2);

        assertThat(authorRepository.checkAuthorByIdHavingCoAuthoredBooks(a1.getId())).isFalse();
        assertThat(authorRepository.checkAuthorByIdHavingCoAuthoredBooks(a2.getId())).isTrue();

    }

}
