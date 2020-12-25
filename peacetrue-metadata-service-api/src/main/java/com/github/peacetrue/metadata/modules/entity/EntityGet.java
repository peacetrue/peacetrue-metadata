package com.github.peacetrue.metadata.modules.entity;

import com.github.peacetrue.core.OperatorCapableImpl;
import lombok.*;


/**
 * @author xiayx
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EntityGet extends OperatorCapableImpl<Long> {

    private static final long serialVersionUID = 0L;

    private Long id;
    private String code;

    public EntityGet(Long id) {
        this.id = id;
    }

    public EntityGet(String code) {
        this.code = code;
    }
}
