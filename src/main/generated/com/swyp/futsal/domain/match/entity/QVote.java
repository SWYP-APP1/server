package com.swyp.futsal.domain.match.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVote is a Querydsl query type for Vote
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVote extends EntityPathBase<Vote> {

    private static final long serialVersionUID = -641027119L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVote vote = new QVote("vote");

    public final com.swyp.futsal.domain.common.QBaseEntity _super = new com.swyp.futsal.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdTime = _super.createdTime;

    public final StringPath id = createString("id");

    public final QMatch match;

    public final EnumPath<com.swyp.futsal.domain.common.enums.ParticipationStatus> participationChoice = createEnum("participationChoice", com.swyp.futsal.domain.common.enums.ParticipationStatus.class);

    public final com.swyp.futsal.domain.team.entity.QTeamMember targetTeamMember;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedTime = _super.updatedTime;

    public final com.swyp.futsal.domain.team.entity.QTeamMember voterTeamMember;

    public final EnumPath<com.swyp.futsal.domain.common.enums.VoteType> voteType = createEnum("voteType", com.swyp.futsal.domain.common.enums.VoteType.class);

    public QVote(String variable) {
        this(Vote.class, forVariable(variable), INITS);
    }

    public QVote(Path<? extends Vote> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVote(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVote(PathMetadata metadata, PathInits inits) {
        this(Vote.class, metadata, inits);
    }

    public QVote(Class<? extends Vote> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.match = inits.isInitialized("match") ? new QMatch(forProperty("match"), inits.get("match")) : null;
        this.targetTeamMember = inits.isInitialized("targetTeamMember") ? new com.swyp.futsal.domain.team.entity.QTeamMember(forProperty("targetTeamMember"), inits.get("targetTeamMember")) : null;
        this.voterTeamMember = inits.isInitialized("voterTeamMember") ? new com.swyp.futsal.domain.team.entity.QTeamMember(forProperty("voterTeamMember"), inits.get("voterTeamMember")) : null;
    }

}

