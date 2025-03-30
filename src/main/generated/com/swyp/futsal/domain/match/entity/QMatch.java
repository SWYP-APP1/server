package com.swyp.futsal.domain.match.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMatch is a Querydsl query type for Match
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMatch extends EntityPathBase<Match> {

    private static final long serialVersionUID = 1594267070L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMatch match = new QMatch("match");

    public final com.swyp.futsal.domain.common.QBaseEntity _super = new com.swyp.futsal.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdTime = _super.createdTime;

    public final StringPath description = createString("description");

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final StringPath id = createString("id");

    public final StringPath location = createString("location");

    public final StringPath matchDate = createString("matchDate");

    public final StringPath opponentTeamName = createString("opponentTeamName");

    public final NumberPath<Integer> rounds = createNumber("rounds", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public final EnumPath<com.swyp.futsal.domain.common.enums.MatchStatus> status = createEnum("status", com.swyp.futsal.domain.common.enums.MatchStatus.class);

    public final StringPath substituteTeamMemberId = createString("substituteTeamMemberId");

    public final com.swyp.futsal.domain.team.entity.QTeam team;

    public final EnumPath<com.swyp.futsal.domain.common.enums.MatchType> type = createEnum("type", com.swyp.futsal.domain.common.enums.MatchType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedTime = _super.updatedTime;

    public final EnumPath<com.swyp.futsal.domain.common.enums.VoteStatus> voteStatus = createEnum("voteStatus", com.swyp.futsal.domain.common.enums.VoteStatus.class);

    public QMatch(String variable) {
        this(Match.class, forVariable(variable), INITS);
    }

    public QMatch(Path<? extends Match> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMatch(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMatch(PathMetadata metadata, PathInits inits) {
        this(Match.class, metadata, inits);
    }

    public QMatch(Class<? extends Match> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.team = inits.isInitialized("team") ? new com.swyp.futsal.domain.team.entity.QTeam(forProperty("team"), inits.get("team")) : null;
    }

}

