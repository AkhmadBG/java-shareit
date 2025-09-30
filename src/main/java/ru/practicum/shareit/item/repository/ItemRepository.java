package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemByOwnerId(Long userId);

    @Query("SELECT i FROM Item i WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%'))) " +
            "AND i.available = true")
    List<Item> searchItemsByText(String text);

    @Query("SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.owner " +
            "LEFT JOIN FETCH i.comments " +
            "LEFT JOIN FETCH i.request " +
            "WHERE i.id = :itemId")
    Optional<Item> findByIdWithOwnerAndRequest(Long itemId);

}