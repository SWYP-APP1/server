package com.swyp.futsal.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -1744189298L;

    public static final QUser user = new QUser("user");

    public final com.swyp.futsal.domain.common.QBaseEntity _super = new com.swyp.futsal.domain.common.QBaseEntity(this);

    public final BooleanPath agreement = createBoolean("agreement");

    public final StringPath birthDate = createString("birthDate");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdTime = _super.createdTime;

    public final StringPath email = createString("email");

    public final EnumPath<com.swyp.futsal.domain.common.enums.Gender> gender = createEnum("gender", com.swyp.futsal.domain.common.enums.Gender.class);

    public final StringPath id = createString("id");

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final StringPath name = createString("name");

    public final BooleanPath notification = createBoolean("notification");

    public final EnumPath<com.swyp.futsal.domain.common.enums.Platform> platform = createEnum("platform", com.swyp.futsal.domain.common.enums.Platform.class);

    public final StringPath profileUri = createString("profileUri");

    public final NumberPath<Integer> squadNumber = createNumber("squadNumber", Integer.class);

    public final StringPath uid = createString("uid");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedTime = _super.updatedTime;

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

