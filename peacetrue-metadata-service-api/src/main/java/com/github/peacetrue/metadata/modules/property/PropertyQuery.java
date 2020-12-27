package com.github.peacetrue.metadata.modules.property;

import com.github.peacetrue.core.OperatorCapableImpl;
import com.github.peacetrue.core.Range;
import lombok.*;


/**
 * @author xiayx
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PropertyQuery extends OperatorCapableImpl<Long> {

    public static final PropertyQuery DEFAULT = new PropertyQuery();

    private static final long serialVersionUID = 0L;

    /** 主键 */
    private Long[] id;
    /** 实体. 属性所属实体 */
    private Long entityId;
    /** 编码 */
    private String code;
    /** 名称 */
    private String name;
    /** 类型. 关联字典主键 */
    private Long typeId;
    /** 关联实体. 关联的实体，若无设置为 0 */
    private Long referenceId;
    /** 备注 */
    private String remark;
    /** 序号 */
    private Integer serialNumber;
    /** 创建者主键 */
    private Long creatorId;
    /** 创建时间 */
    private Range.LocalDateTime createdTime;
    /** 修改者主键 */
    private Long modifierId;
    /** 最近修改时间 */
    private Range.LocalDateTime modifiedTime;

    public PropertyQuery(Long[] id) {
        this.id = id;
    }

}
