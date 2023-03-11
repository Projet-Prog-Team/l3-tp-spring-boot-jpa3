package fr.uga.l3miage.library.data.repo;

import fr.uga.l3miage.library.data.domain.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
class BorrowRepositoryTest extends Base {   

    @Autowired
    EntityManager entityManager;

    @Autowired
    BorrowRepository repository;
    private Book b1;
    private Book b2;
    private Book b3;
    private User u1;
    private User u2;
    private Librarian l1;

    @BeforeEach
    void setupInventory() {
        Author a1 = Fixtures.newAuthor();
        Author a2 = Fixtures.newAuthor();
        Author a3 = Fixtures.newAuthor();

        this.b1 = Fixtures.newBook();
        this.b2 = Fixtures.newBook();
        this.b3 = Fixtures.newBook();
        a1.addBook(b1);
        a2.addBook(b2);
        a3.addBook(b3);
        entityManager.persist(a1);
        entityManager.persist(a2);
        entityManager.persist(a3);

        b1.addAuthor(a1);
        b2.addAuthor(a2);
        b3.addAuthor(a3);
        entityManager.persist(b1);
        entityManager.persist(b2);
        entityManager.persist(b3);

        this.u1 = Fixtures.newUser();
        this.u2 = Fixtures.newUser();
        this.l1 = Fixtures.newLibrarian();

        entityManager.persist(u1);
        entityManager.persist(u2);
        entityManager.persist(l1);

        entityManager.flush();
    }

    @Test
    void all(){

        //On crée des emprunts
        Borrow borrow1 = Fixtures.newBorrow(u1, l1, b1);
        Borrow borrow2 = Fixtures.newBorrow(u1, l1, b2);
        Borrow borrow3 = Fixtures.newBorrow(u1, l1, b3);
        
        entityManager.persist(borrow1);
        entityManager.persist(borrow2);
        entityManager.persist(borrow3);
        entityManager.flush();

        // Appeler la méthode all() pour récupérer tous les emprunts
        List<Borrow> allBorrows = repository.all();

        // Vérifier que la méthode a renvoyé tous les emprunts ajoutés à la base de données
        assertThat(allBorrows.size()).isEqualTo(3);
        assertThat(allBorrows.get(0)).isEqualTo(borrow1);
        assertThat(allBorrows.get(1)).isEqualTo(borrow2);
        assertThat(allBorrows.get(2)).isEqualTo(borrow3);
    }

    @Test
    void findInProgressByUser() {

        //On crée des emprunts en cours
        Borrow inProgress = Fixtures.newBorrow(u1, l1, b1, b2);
        Borrow finished = Fixtures.newBorrow(u1, l1, b3);
        finished.setRequestedReturn(new Date());
        finished.setFinished(true);
        entityManager.persist(inProgress);
        entityManager.persist(finished);
        entityManager.flush();

        // Appeler la méthode findInProgressByUser(String id) pour récupérer tous les emprunts
        List<Borrow> progressByUser = repository.findInProgressByUser(u1.getId());

        //On vérifie que la méthode a renvoyé le bon résultat
        assertThat(progressByUser).containsExactly(inProgress);

    }

    @Test
    void countCurrentBorrowedBooksByUser() {

        //Oncrée des emprunts pour un utilisateur
        Borrow borrow1 = Fixtures.newBorrow(u1, l1, b1);
        borrow1.setFinished(false);
        Borrow borrow2 = Fixtures.newBorrow(u1, l1, b2);
        borrow2.setFinished(false);
        Borrow borrow3 = Fixtures.newBorrow(u1, l1, b3);
        borrow3.setFinished(false);

        entityManager.persist(borrow1);
        entityManager.persist(borrow2);
        entityManager.persist(borrow3);

        entityManager.flush();

        // On compte le nombre d'emprunts en cours de l'utilisateur u1 en appelant la fonction 
        //countCurrentBorrowBookByUser(Long id)
        int count = (repository.countCurrentBorrowedBooksByUser(u1.getId()));

        // On vérifie que le résultat renvoyé est correct
        assertThat(count).isEqualTo(3);

    }

    

    @Test
    void countBorrowedBooksByUser() {

        //On crée des emprunts pour un utilisateur
        Borrow borrow4 = Fixtures.newBorrow(u2, l1, b1);
        Borrow borrow5 = Fixtures.newBorrow(u2, l1, b2);
        Borrow borrow6 = Fixtures.newBorrow(u2, l1, b3);

        entityManager.persist(borrow4);
        entityManager.persist(borrow5);
        entityManager.persist(borrow6);

        entityManager.flush();

        //On appelle la méthode countBorrowBookByUser(Long id)
        int count = (repository.countBorrowedBooksByUser(u2.getId()));

        //On vérifie qu'on renvoie le bon résultat
        assertThat(count).isEqualTo(3);

    }

    @Test
    void foundAllLateBorrow() {

        //On crée des emprunts en retard et un emprunt en avance
        Borrow lateBorrow1 = Fixtures.newBorrow(u1, l1, b1);
        lateBorrow1.setRequestedReturn(Date.from(ZonedDateTime.now().minus(5, ChronoUnit.DAYS).toInstant()));
        lateBorrow1.setFinished(false);
        Borrow lateBorrow2 = Fixtures.newBorrow(u2, l1, b2);
        lateBorrow2.setRequestedReturn(Date.from(ZonedDateTime.now().minus(10, ChronoUnit.DAYS).toInstant()));
        lateBorrow2.setFinished(false);
        Borrow lateBorrow3 = Fixtures.newBorrow(u1, l1, b3);
        lateBorrow3.setRequestedReturn(Date.from(ZonedDateTime.now().minus(15, ChronoUnit.DAYS).toInstant()));
        lateBorrow3.setFinished(false);
        Borrow onTimeBorrow = Fixtures.newBorrow(u2, l1, b1, b2);
        onTimeBorrow.setRequestedReturn(Date.from(ZonedDateTime.now().plus(3, ChronoUnit.DAYS).toInstant()));
        onTimeBorrow.setFinished(false);
    
        entityManager.persist(lateBorrow1);
        entityManager.persist(lateBorrow2);
        entityManager.persist(lateBorrow3);
        entityManager.persist(onTimeBorrow);
        entityManager.flush();
    
        //On appelle la méthode foundAllLateBorrow() pour récupérer tous les emprunts en retard
        List<Borrow> lateBorrows = repository.foundAllLateBorrow();
        assertThat(lateBorrows).containsExactlyInAnyOrder(lateBorrow1, lateBorrow2, lateBorrow3);

    }

    @Test
     void foundAllBorrowThatWillBeLateInDays() {

        //On crée des emprunts en retard pour un utilisateur
        Borrow lateIn5Days = Fixtures.newBorrow(u1, l1, b1);
        lateIn5Days.setRequestedReturn(Date.from(ZonedDateTime.now().plus(5, ChronoUnit.DAYS).toInstant()));
        Borrow lateIn10Days = Fixtures.newBorrow(u1, l1, b2);
        lateIn10Days.setRequestedReturn(Date.from(ZonedDateTime.now().plus(10, ChronoUnit.DAYS).toInstant()));
        Borrow lateIn15Days = Fixtures.newBorrow(u2, l1, b3);
        lateIn15Days.setRequestedReturn(Date.from(ZonedDateTime.now().plus(15, ChronoUnit.DAYS).toInstant()));

        entityManager.persist(lateIn5Days);
        entityManager.persist(lateIn10Days);
        entityManager.persist(lateIn15Days);
        entityManager.flush();

        //On appelle la méthode findAllBorrowThatWillLateWithin(int days) 
        List<Borrow> borrows = repository.findAllBorrowThatWillLateWithin(12);

        //On vérifie que le résultat renvoyé est correct 
        assertThat(borrows).containsExactlyInAnyOrder(lateIn5Days, lateIn10Days);

    }

}
