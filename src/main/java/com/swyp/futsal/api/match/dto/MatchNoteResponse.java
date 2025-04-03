package com.swyp.futsal.api.match.dto;

import com.swyp.futsal.domain.match.entity.MatchNote;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MatchNoteResponse {
    private String id;
    private String matchId;
    private String description;
    private List<Photo> photos;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    @Getter
    @Builder
    public static class Photo {
        private String url;
        private String uri;
    }

    public static MatchNoteResponse from(MatchNote note, List<Photo> photos) {
        return MatchNoteResponse.builder()
                .id(note.getId())
                .matchId(note.getMatch().getId())
                .description(note.getDescription())
                .photos(photos)
                .createdTime(note.getCreatedTime())
                .updatedTime(note.getUpdatedTime())
                .build();
    }
} 