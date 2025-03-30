package com.swyp.futsal.domain.match.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMatchStats is a Querydsl query type for MatchStats
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMatchStats extends EntityPathBase<MatchStats> {

    private static final long serialVersionUID = -1613814399L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMatchStats matchStats = new QMatchStats("matchStats");

    public final com.swyp.futsal.domain.common.QBaseEntity _super = new com.swyp.futsal.domain.common.QBaseEntity(this);

    public final StringPath assistedMatchStatId = createString("assistedMatchStatId");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdTime = _super.createdTime;

    public final DateTimePath<java.time.LocalDateTime> historyTime = createDateTime("historyTime", java.time.LocalDateTime.class);

    public final StringPath id = createString("id");

    public final QMatch match;

    public final QMatchParticipant matchParticipant;

    public final NumberPath<Integer> roundNumber = createNumber("roundNumber", Integer.class);

    public final EnumPath<com.swyp.futsal.domain.common.enums.StatType> statType = createEnum("statType", com.swyp.futsal.domain.common.enums.StatType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedTime = _super.updatedTime;

    public QMatchStats(String variable) {
        this(MatchStats.class, forVariable(variable), INITS);
    }

    public QMatchStats(Path<? extends MatchStats> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMatchStats(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMatchStats(PathMetadata metadata, PathInits inits) {
        this(MatchStats.class, metadata, inits);
    }

    public QMatchStats(Class<? extends MatchStats> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.match = inits.isInitialized("match") ? new QMatch(forProperty("match"), inits.get("match")) : null;
        this.matchParticipant = inits.isInitialized("matchParticipant") ? new QMatchParticipant(forProperty("matchParticipant"), inits.get("matchParticipant")) : null;
    }

}

