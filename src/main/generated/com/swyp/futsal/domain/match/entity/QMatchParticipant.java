package com.swyp.futsal.domain.match.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMatchParticipant is a Querydsl query type for MatchParticipant
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMatchParticipant extends EntityPathBase<MatchParticipant> {

    private static final long serialVersionUID = -847602027L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMatchParticipant matchParticipant = new QMatchParticipant("matchParticipant");

    public final com.swyp.futsal.domain.common.QBaseEntity _super = new com.swyp.futsal.domain.common.QBaseEntity(this);

    public final NumberPath<Integer> assists = createNumber("assists", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdTime = _super.createdTime;

    public final DateTimePath<java.time.LocalDateTime> endTime = createDateTime("endTime", java.time.LocalDateTime.class);

    public final NumberPath<Integer> goals = createNumber("goals", Integer.class);

    public final StringPath id = createString("id");

    public final QMatch match;

    public final StringPath position = createString("position");

    public final DateTimePath<java.time.LocalDateTime> startTime = createDateTime("startTime", java.time.LocalDateTime.class);

    public final EnumPath<com.swyp.futsal.domain.common.enums.ParticipationStatus> status = createEnum("status", com.swyp.futsal.domain.common.enums.ParticipationStatus.class);

    public final EnumPath<com.swyp.futsal.domain.common.enums.SubTeam> subTeam = createEnum("subTeam", com.swyp.futsal.domain.common.enums.SubTeam.class);

    public final com.swyp.futsal.domain.team.entity.QTeamMember teamMember;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedTime = _super.updatedTime;

    public QMatchParticipant(String variable) {
        this(MatchParticipant.class, forVariable(variable), INITS);
    }

    public QMatchParticipant(Path<? extends MatchParticipant> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMatchParticipant(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMatchParticipant(PathMetadata metadata, PathInits inits) {
        this(MatchParticipant.class, metadata, inits);
    }

    public QMatchParticipant(Class<? extends MatchParticipant> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.match = inits.isInitialized("match") ? new QMatch(forProperty("match"), inits.get("match")) : null;
        this.teamMember = inits.isInitialized("teamMember") ? new com.swyp.futsal.domain.team.entity.QTeamMember(forProperty("teamMember"), inits.get("teamMember")) : null;
    }

}

