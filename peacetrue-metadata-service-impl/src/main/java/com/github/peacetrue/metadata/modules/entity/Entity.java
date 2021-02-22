package com.github.peacetrue.metadata.modules.entity;

import com.github.peacetrue.core.IdCapable;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体类
 *
 * @author xiayx
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Entity implements Serializable, IdCapable<Long> {

    private static final long serialVersionUID = 0L;

    /** 主键 */
    @Id
    private Long id;
    /** 编码 */
    private String code;
    /** 名称 */
    private String name;
    /** 多对多关联 */
    private Boolean manyToMany;
    /** 备注 */
    private String remark;
    /** 序号 */
    private Long serialNumber;
    /** 创建者主键 */
    private Long creatorId;
    /** 创建时间 */
    private LocalDateTime createdTime;
    /** 修改者主键 */
    private Long modifierId;
    /** 最近修改时间 */
    private LocalDateTime modifiedTime;

}
