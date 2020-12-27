package com.github.peacetrue.metadata.clazz;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

/** 自引用 */
@Data
@EntityMetadata(desc = "用户")
public class UserSelfReference implements Serializable {
    private static final long serialVersionUID = 0L;
    @Id
    private Long id;
    private String username;
    private String password;
    @PropertyMetadata(desc = "创建者", reference = "com.github.peacetrue.metadata.clazz.UserSelfReference")
    private Long creatorId;
    private LocalDateTime createdTime;
    private Long modifierId;
    private LocalDateTime modifiedTime;

}
