package com.advance.dataloader.common.base;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author Advance
 * @date 2021年10月14日 15:27
 * @since V1.0.0
 */
@Data
@ToString
public class BaseOracleSqlModelPK implements Serializable {

    private static final long serialVersionUID = 1227225159376390524L;

    private String retvalue;

}
