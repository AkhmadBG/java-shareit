package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT DISTINCT r FROM Request r " +
            "LEFT JOIN FETCH r.items i " +
            "LEFT JOIN FETCH i.owner " +
            "WHERE r.requestor.id = :userId " +
            "ORDER BY r.created DESC")
    List<Request> getRequestsByRequestorIdOrderByCreatedDesc(Long userId);

    @Query("SELECT DISTINCT r FROM Request r " +
            "LEFT JOIN FETCH r.items i " +
            "LEFT JOIN FETCH i.owner " +
            "WHERE r.requestor.id <> :userId " +
            "ORDER BY r.created DESC")
    List<Request> getRequestsByIdNotOrderByCreatedDesc(Long userId);

}