package com.swyp.futsal.domain.match.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMatchNote is a Querydsl query type for MatchNote
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMatchNote extends EntityPathBase<MatchNote> {

    private static final long serialVersionUID = -1437685040L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMatchNote matchNote = new QMatchNote("matchNote");

    public final com.swyp.futsal.domain.common.QBaseEntity _super = new com.swyp.futsal.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdTime = _super.createdTime;

    public final StringPath description = createString("description");

    public final StringPath id = createString("id");

    public final QMatch match;

    public final ListPath<String, StringPath> photoUris = this.<String, StringPath>createList("photoUris", String.class, StringPath.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedTime = _super.updatedTime;

    public final com.swyp.futsal.domain.user.entity.QUser user;

    public QMatchNote(String variable) {
        this(MatchNote.class, forVariable(variable), INITS);
    }

    public QMatchNote(Path<? extends MatchNote> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMatchNote(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMatchNote(PathMetadata metadata, PathInits inits) {
        this(MatchNote.class, metadata, inits);
    }

    public QMatchNote(Class<? extends MatchNote> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.match = inits.isInitialized("match") ? new QMatch(forProperty("match"), inits.get("match")) : null;
        this.user = inits.isInitialized("user") ? new com.swyp.futsal.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

