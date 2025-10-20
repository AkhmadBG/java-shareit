package ru.practicum.shareit.request.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User requestor;

    @Column(name = "created")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Время и дата не может быть в прошлом")
    private LocalDateTime created;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "request")
    private List<Item> items = new ArrayList<>();

}