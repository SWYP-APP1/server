package com.swyp.futsal.domain.match.entity;

import com.swyp.futsal.domain.common.BaseEntity;
import com.swyp.futsal.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "match_note")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchNote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String description;

    @ElementCollection
    @CollectionTable(name = "match_note_photos", joinColumns = @JoinColumn(name = "match_note_id"))
    @Column(name = "photo_uri")
    private List<String> photoUris;

    public void updateDescription(String description) {
        this.description = description;
    }
} 