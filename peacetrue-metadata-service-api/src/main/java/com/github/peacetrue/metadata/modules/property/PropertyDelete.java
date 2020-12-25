package com.github.peacetrue.metadata.modules.property;

import com.github.peacetrue.core.IdCapable;
import com.github.peacetrue.core.OperatorCapableImpl;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author xiayx
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDelete extends OperatorCapableImpl<Long> implements IdCapable<Long> {

    private static final long serialVersionUID = 0L;

    @NotNull
    @Min(1)
    private Long id;

}
