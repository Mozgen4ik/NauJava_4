package ru.Artem.Vinyl.crud_repos;

import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import ru.Artem.Vinyl.entity.VinylRecord;
import ru.Artem.Vinyl.entity.Genre;

import java.util.ArrayList;
import java.util.List;

/**
 * Реализация кастомного репозитория с использованием Criteria API.
 * Содержит 2 метода, что и VinylRecordRepository, но через Criteria API.
 */

public class VinylRecordRepositoryImpl implements VinylRecordRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    /**
     * Criteria API реализация метода findByYearBetweenAndPriceLessThan.
     */
    @Override
    public List<VinylRecord> findByYearBetweenAndPriceLessThanCriteria(Integer startYear, Integer endYear, Float maxPrice) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<VinylRecord> cq = cb.createQuery(VinylRecord.class);
        Root<VinylRecord> root = cq.from(VinylRecord.class);

        List<Predicate> predicates = new ArrayList<>();

        // Условие: year BETWEEN startYear AND endYear
        if (startYear != null && endYear != null) {
            predicates.add(cb.between(root.get("year"), startYear, endYear));
        }

        // Условие: price < maxPrice
        if (maxPrice != null) {
            predicates.add(cb.lessThan(root.get("price"), maxPrice));
        }

        cq.select(root).where(predicates.toArray(new Predicate[0]));
        TypedQuery<VinylRecord> query = em.createQuery(cq);
        return query.getResultList();
    }

    /**
     * Criteria API реализация метода findByGenreName.
     */
    @Override
    public List<VinylRecord> findByGenreNameCriteria(String genreName) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<VinylRecord> cq = cb.createQuery(VinylRecord.class);
        Root<VinylRecord> root = cq.from(VinylRecord.class);

        // Join к связанной сущности Genre через поле "genre"
        Join<VinylRecord, Genre> genreJoin = root.join("genre", JoinType.INNER);

        // Условие: имя жанра равно genreName
        Predicate genrePredicate = cb.equal(genreJoin.get("name"), genreName);

        cq.select(root).where(genrePredicate);
        TypedQuery<VinylRecord> query = em.createQuery(cq);
        return query.getResultList();
    }
}