package fr.uga.l3miage.library.data.repo;

import fr.uga.l3miage.library.data.domain.Borrow;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

//import java.sql.Date;
import java.util.Date;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Repository
public class BorrowRepository implements CRUDRepository<String, Borrow> {

    private final EntityManager entityManager;

    @Autowired
    public BorrowRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Borrow save(Borrow entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public Borrow get(String id) {
        return entityManager.find(Borrow.class, id);
    }

    @Override
    public void delete(Borrow entity) {
        entityManager.remove(entity);
    }

    @Override
    public List<Borrow> all() {
        return entityManager.createQuery("from Borrow", Borrow.class).getResultList();
    }

    /**
     * Trouver des emprunts en cours pour un emprunteur donné
     *
     * @param userId l'id de l'emprunteur
     * @return la liste des emprunts en cours
     */
    public List<Borrow> findInProgressByUser(Long userId) {

        //requête qui recherche tous les emprunts en cours de l'utilisateur d'id userId
        String query = "SELECT b FROM Borrow b JOIN b.borrower u WHERE u.id = :userId AND b.finished = false ";

        return entityManager.createQuery(query, Borrow.class)
            .setParameter("userId", userId)     //On spécifie l'id de l'utilisateur dans la requête
            .getResultList();             //On renvoie la liste obtenue
    }

    /**
     * Compte le nombre total de livres emprunté par un utilisateur.
     *
     * @param userId l'id de l'emprunteur
     * @return le nombre de livre
     */
    public int countBorrowedBooksByUser(Long userId) {

        //Requête qui compte le nombre total de livres emrpuntés par l'utilisateur d'id userId
        String query = "SELECT COUNT(b) FROM Borrow b JOIN b.borrower u WHERE u.id = :userId";

        return entityManager.createQuery(query,  Long.class).
        setParameter("userId", userId).      //On spécifie l'id de l'utilisateur dans la requête
        getSingleResult().         //On obtient le nombre d'emprunts en Long
        intValue();        //On convertit en int (conformément à la signature de la fonction)
    } 

    /**
     * Compte le nombre total de livres non rendu par un utilisateur.
     *
     * @param userId l'id de l'emprunteur
     * @return le nombre de livre
     */
    public int countCurrentBorrowedBooksByUser(Long userId) {

         //Requête qui compte le nombre total de livres non rendus par l'utilisateur d'id userId
         String query = "SELECT COUNT(b) FROM Borrow b JOIN b.borrower u WHERE u.id = :userId AND b.finished = false";

         return entityManager.createQuery(query, Long.class).
         setParameter("userId", userId).         //On spécifie l'id de l'utilisateur dans la requête
         getSingleResult().          //On obtient le nombre d'emprunts non rendus en Long
         intValue();                 //On convertit en int (conformément à la signture de la fonction)
        
    }

    /**
     * Recherche tous les emprunt en retard trié
     *
     * @return la liste des emprunt en retard
     */
    public List<Borrow> foundAllLateBorrow() {
        
        //Requête qui recherche tous les emprunts en retard et triés : on compare la date de retour prévue de l'emprunt 
        //à la date courante. Si elle est inférieure, c'est que l'emprunt est en retard.
        String query = "SELECT b FROM Borrow b WHERE b.requestedReturn < CURRENT_DATE ORDER BY b.requestedReturn ASC";

        //On renvoie la liste de ces emprunts
        return entityManager.createQuery(query, Borrow.class).
        getResultList();
    }

    /**
     * Calcul les emprunts qui seront en retard entre maintenant et x jours.
     *
     * @param days le nombre de jour avant que l'emprunt soit en retard
     * @return les emprunt qui sont bientôt en retard
     */
    public List<Borrow> findAllBorrowThatWillLateWithin(int days) {
        //On rajoute le nombre de jours restant à la date actuelle du jour 
        Date dueDate = Date.from(ZonedDateTime.now().plus(days, ChronoUnit.DAYS).toInstant());

        //Requête qui calcule les emprunts qui seront en retard entre la date du jour et "days" jours
        String query = "SELECT b FROM Borrow b WHERE b.requestedReturn <= :dueDate";
        
        return entityManager.createQuery(query, Borrow.class)
        .setParameter("dueDate", dueDate)
        .getResultList();
    }

}