package com.github.peacetrue.metadata.modules.entity;

import com.github.peacetrue.core.OperatorCapableImpl;
import com.github.peacetrue.metadata.modules.property.PropertyAdd;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author xiayx
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EntityAdd extends OperatorCapableImpl<Long> {

    private static final long serialVersionUID = 0L;

    /** 编码 */
    @NotNull
    @Size(min = 1, max = 64)
    private String code;
    /** 名称 */
    @NotNull
    @Size(min = 1, max = 255)
    private String name;
    /** 多对多关联 */
    private Boolean manyToMany;
    /** 备注 */
    @Size(min = 1, max = 255)
    private String remark;
    /** 序号 */
    private Long serialNumber;
    /** 属性集合 */
    private List<PropertyAdd> properties;

    public Set<EntityAdd> getSelfAndReferences() {
        Set<EntityAdd> entityAdds = new HashSet<>();
        this.addSelfAndReferences(entityAdds);
        return entityAdds;
    }

    public void addSelfAndReferences(Set<EntityAdd> entityAdds) {
        entityAdds.add(this);
        if (properties == null) return;
        for (PropertyAdd property : properties) {
            EntityAdd entityAdd = property.getReference();
            if (entityAdd != null && !entityAdds.contains(entityAdd)) {
                entityAdd.addSelfAndReferences(entityAdds);
            }
        }
    }
}
