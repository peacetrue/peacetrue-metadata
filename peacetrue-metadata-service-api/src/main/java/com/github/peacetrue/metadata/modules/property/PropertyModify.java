package com.github.peacetrue.metadata.modules.property;

import com.github.peacetrue.core.IdCapable;
import com.github.peacetrue.core.OperatorCapableImpl;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * @author xiayx
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PropertyModify extends OperatorCapableImpl<Long> implements IdCapable<Long> {

    private static final long serialVersionUID = 0L;

    /** 主键 */
    @NotNull
    private Long id;
    /** 实体. 属性所属实体 */
    private Long entityId;
    /** 编码 */
    @Size(min = 1, max = 32)
    private String code;
    /** 名称 */
    @Size(min = 1, max = 255)
    private String name;
    /** 类型. 关联字典主键 */
    private Long typeId;
    /** 关联实体. 关联的实体，若无设置为 0 */
    private Long associateEntityId;
    /** 备注 */
    @Size(min = 1, max = 255)
    private String remark;
    /** 序号 */
    private Integer serialNumber;

}
