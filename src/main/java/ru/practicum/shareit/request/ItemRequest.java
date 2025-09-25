package ru.practicum.shareit.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class ItemRequest {

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

}