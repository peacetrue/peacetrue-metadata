package com.github.peacetrue.metadata.clazz;

import com.github.peacetrue.core.IdCapable;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @author : xiayx
 * @since : 2020-12-26 13:26
 **/
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserCrossReference implements Serializable, IdCapable<Long> {

    private static final long serialVersionUID = 0L;

    /** 主键 */
    @Id
    private Long id;
    /** 实体主键 */
    @PropertyMetadata(desc = "实体", reference = "com.github.peacetrue.metadata.clazz.EntityCrossReference")
    private Long entityId;
    /** 创建者主键 */
    @PropertyMetadata(desc = "创建者", reference = "com.github.peacetrue.metadata.clazz.UserCrossReference")
    private Long creatorId;


}
