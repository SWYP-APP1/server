package com.swyp.futsal.domain.team.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeam is a Querydsl query type for Team
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeam extends EntityPathBase<Team> {

    private static final long serialVersionUID = -1670305102L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeam team = new QTeam("team");

    public final com.swyp.futsal.domain.common.QBaseEntity _super = new com.swyp.futsal.domain.common.QBaseEntity(this);

    public final EnumPath<com.swyp.futsal.domain.common.enums.TeamRole> access = createEnum("access", com.swyp.futsal.domain.common.enums.TeamRole.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdTime = _super.createdTime;

    public final NumberPath<Integer> dues = createNumber("dues", Integer.class);

    public final StringPath id = createString("id");

    public final StringPath introduction = createString("introduction");

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final StringPath logoUri = createString("logoUri");

    public final EnumPath<com.swyp.futsal.domain.common.enums.MatchType> matchType = createEnum("matchType", com.swyp.futsal.domain.common.enums.MatchType.class);

    public final StringPath name = createString("name");

    public final StringPath rule = createString("rule");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedTime = _super.updatedTime;

    public final com.swyp.futsal.domain.user.entity.QUser user;

    public QTeam(String variable) {
        this(Team.class, forVariable(variable), INITS);
    }

    public QTeam(Path<? extends Team> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeam(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeam(PathMetadata metadata, PathInits inits) {
        this(Team.class, metadata, inits);
    }

    public QTeam(Class<? extends Team> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.swyp.futsal.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

