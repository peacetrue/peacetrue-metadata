package com.github.peacetrue.metadata.modules.property;

import com.github.peacetrue.core.IdCapable;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 属性实体类
 *
 * @author xiayx
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Property implements Serializable, IdCapable<Long> {

    private static final long serialVersionUID = 0L;

    /** 主键 */
    @Id
    private Long id;
    /** 实体. 属性所属实体 */
    private Long entityId;
    /** 编码 */
    private String code;
    /** 名称 */
    private String name;
    /** 类型. 关联字典主键 */
    private Long typeId;
    /** 关联实体. 关联的实体，若无设置为 0 */
    private Long associateEntityId;
    /** 备注 */
    private String remark;
    /** 序号 */
    private Integer serialNumber;
    /** 创建者主键 */
    private Long creatorId;
    /** 创建时间 */
    private LocalDateTime createdTime;
    /** 修改者主键 */
    private Long modifierId;
    /** 最近修改时间 */
    private LocalDateTime modifiedTime;

}
