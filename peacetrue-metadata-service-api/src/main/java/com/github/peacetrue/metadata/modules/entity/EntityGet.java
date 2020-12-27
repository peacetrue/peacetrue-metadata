package com.github.peacetrue.metadata.modules.entity;

import com.github.peacetrue.core.OperatorCapableImpl;
import lombok.*;


/**
 * 2 种方式获取 {@link EntityVO}：
 * <ul>
 *     <li>通过 {@link #id} 获取</li>
 *     <li>通过 {@link #code} 获取</li>
 * </ul>
 *
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
