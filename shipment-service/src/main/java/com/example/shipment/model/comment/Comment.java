package com.example.shipment.model.comment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comments")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Comment {

    protected Comment() {
    }

    protected Comment(
            UUID commentId,
            String commentText,
            LocalDateTime commentDate,
            String addedBy) {

        this.commentId = commentId;
        this.commentText = commentText;
        this.commentDate = commentDate;
        this.addedBy = addedBy;
    }

    @Id
    @Column(name = "comment_id")
    private UUID commentId;

    @Column(name = "comment_text", nullable = false)
    private String commentText;

    @Column(name = "comment_date", nullable = false)
    private LocalDateTime commentDate;

    @Column(name = "added_by")
    private String addedBy;

}
