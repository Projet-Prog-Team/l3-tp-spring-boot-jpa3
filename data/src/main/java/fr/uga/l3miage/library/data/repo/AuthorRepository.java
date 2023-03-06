package fr.uga.l3miage.library.data.repo;

import fr.uga.l3miage.library.data.domain.Author;
import fr.uga.l3miage.library.data.domain.Book;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuthorRepository implements CRUDRepository<Long, Author> {

    private final EntityManager entityManager;

    @Autowired
    public AuthorRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Author save(Author author) {
        entityManager.persist(author);
        return author;
    }

    @Override
    public Author get(Long id) {
        return entityManager.find(Author.class, id);
    }


    @Override
    public void delete(Author author) {
        entityManager.remove(author);
    }

    /**
     * Renvoie tous les auteurs
     *
     * @return une liste d'auteurs trié par nom
     */
    @Override
    public List<Author> all() {
        //query : String contient la requête qui va rechercher les noms d'auteurs dans la table Author et les trier par ordre alphabétique
        String query = "FROM Author a ORDER BY a.fullName";

        //La méthode getResultList() est utilisée pour exécuter la requête et retourner les résultats.
        return entityManager.createQuery(query, Author.class).getResultList();
    }

    /**
     * Recherche un auteur par nom (ou partie du nom) de façon insensible  à la casse.
     *
     * @param namePart tout ou partie du nomde l'auteur
     * @return une liste d'auteurs trié par nom
     */
    public List<Author> searchByName(String namePart) {

        //query : String est la variable qui contient la requête qui va pour sélectionner les auteurs dont le nom (en minuscules) contient la chaîne de caractères spécifiée.
        String query = "SELECT a FROM Author a WHERE LOWER(a.fullName) LIKE LOWER(:namePart) ORDER BY a.fullName";

        // La méthode setParameter() est utilisée pour injecter le paramètre namePart dans la requête.
        // La méthode getResultList() est utilisée pour exécuter la requête et retourner les résultats.
        // Les résultats sont triés par nom.
        return entityManager.createQuery(query, Author.class)
                .setParameter("namePart", "%" + namePart + "%")
                .getResultList();
    }

    /**
     * Recherche si l'auteur a au moins un livre co-écrit avec un autre auteur
     *
     * @return true si l'auteur partage
     */
    public boolean checkAuthorByIdHavingCoAuthoredBooks(long authorId) {
        String query = "SELECT COUNT(*) FROM Book b " +
        "JOIN b.authors a " +
        "WHERE a.id = :authorId " +
        "AND EXISTS (SELECT 1 FROM Book bb JOIN bb.authors aa WHERE aa.id <> a.id AND bb.id = b.id)";
        Long count = entityManager.createQuery(query, Long.class)
                        .setParameter("authorId", authorId)
                        .getSingleResult();
        return count > 0;
    }

}
