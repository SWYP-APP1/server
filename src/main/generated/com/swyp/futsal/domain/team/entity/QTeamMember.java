package com.swyp.futsal.domain.team.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamMember is a Querydsl query type for TeamMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamMember extends EntityPathBase<TeamMember> {

    private static final long serialVersionUID = -626633748L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamMember teamMember = new QTeamMember("teamMember");

    public final com.swyp.futsal.domain.common.QBaseEntity _super = new com.swyp.futsal.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdTime = _super.createdTime;

    public final StringPath id = createString("id");

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final StringPath name = createString("name");

    public final EnumPath<com.swyp.futsal.domain.common.enums.TeamRole> role = createEnum("role", com.swyp.futsal.domain.common.enums.TeamRole.class);

    public final NumberPath<Integer> squadNumber = createNumber("squadNumber", Integer.class);

    public final EnumPath<com.swyp.futsal.domain.common.enums.MemberStatus> status = createEnum("status", com.swyp.futsal.domain.common.enums.MemberStatus.class);

    public final QTeam team;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedTime = _super.updatedTime;

    public final com.swyp.futsal.domain.user.entity.QUser user;

    public QTeamMember(String variable) {
        this(TeamMember.class, forVariable(variable), INITS);
    }

    public QTeamMember(Path<? extends TeamMember> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamMember(PathMetadata metadata, PathInits inits) {
        this(TeamMember.class, metadata, inits);
    }

    public QTeamMember(Class<? extends TeamMember> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.team = inits.isInitialized("team") ? new QTeam(forProperty("team"), inits.get("team")) : null;
        this.user = inits.isInitialized("user") ? new com.swyp.futsal.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

