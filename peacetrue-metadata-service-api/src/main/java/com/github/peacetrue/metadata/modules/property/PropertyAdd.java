package com.github.peacetrue.metadata.modules.property;

import com.github.peacetrue.core.OperatorCapableImpl;
import com.github.peacetrue.metadata.modules.entity.EntityAdd;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * @author xiayx
 */
@Getter
@Setter
@ToString(exclude = "associateEntity")
@NoArgsConstructor
@AllArgsConstructor
public class PropertyAdd extends OperatorCapableImpl<Long> {

    private static final long serialVersionUID = 0L;

    /** 实体. 属性所属实体 */
    @NotNull
    private Long entityId;
    /** 编码 */
    @NotNull
    @Size(min = 1, max = 32)
    private String code;
    /** 名称 */
    @NotNull
    @Size(min = 1, max = 255)
    private String name;
    /** 类型. 关联字典主键 */
    @NotNull
    private Long typeId;
    /** 关联实体. 关联的实体，若无设置为 0 */
    private Long associateEntityId;
    /** 关联实体 */
    private EntityAdd associateEntity;
    /** 备注 */
    @Size(min = 1, max = 255)
    private String remark;
    /** 序号 */
    private Integer serialNumber;

}
