package fr.uga.l3miage.library.data.repo;

import fr.uga.l3miage.library.data.domain.Borrow;
import fr.uga.l3miage.library.data.domain.Librarian;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class LibrarianRepository implements CRUDRepository<String, Librarian> {

    private final EntityManager entityManager;

    @Autowired
    public LibrarianRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Librarian save(Librarian entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public Librarian get(String id) {
        return entityManager.find(Librarian.class, id);
    }

    @Override
    public void delete(Librarian entity) {
        entityManager.remove(entity);
    }

    @Override
    public List<Librarian> all() {
        return entityManager.createQuery("from Librarian", Librarian.class).getResultList();
    }

    /**
     * Récupere les bibliothéquaires ayant enregistré le plus de prêts
     * @return les bibliothéquaires les plus actif
     */
    public List<Librarian> top3WorkingLibrarians() {
        List<Borrow> borrowList = entityManager.createQuery("FROM Borrow", Borrow.class).getResultList();
        List<Librarian> librarianList = new ArrayList<Librarian>();
        List<Librarian> top3Librarian = new ArrayList<Librarian>(3);
        for(int i=0; i<borrowList.size(); i++){
            Librarian l = borrowList.get(i).getLibrarian();
            librarianList.add(l);
        }
        for (int i=0; i<3; i++){
            Librarian hardWorkingLibrarian = getMostFrequentNumber(librarianList);
            top3Librarian.add(hardWorkingLibrarian);
            if (hardWorkingLibrarian != null){
                while (librarianList.contains(hardWorkingLibrarian)) {
                    librarianList.remove(hardWorkingLibrarian);
                }
            }
        }
        return top3Librarian;
    }


    public Librarian getMostFrequentNumber(List<Librarian> librarianList) {
        Librarian MostFrequentLibrarian = null;
        int highestFrequency = 0;
        Map<Librarian, Integer> frequencyMap = new HashMap<>();
    
        // Parcours de la liste pour compter les fréquences
        for (Librarian l : librarianList) {
            int frequency = frequencyMap.getOrDefault(l, 0) + 1;
            frequencyMap.put(l, frequency);
            if (frequency > highestFrequency) {
                highestFrequency = frequency;
                MostFrequentLibrarian = l;
            }
        }
    
        return MostFrequentLibrarian;
    }

}