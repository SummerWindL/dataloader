package com.advance.dataloader.common.base;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

/**
 * @author Advance
 * @date 2021年10月14日 15:26
 * @since V1.0.0
 */
@Data
@Entity
@IdClass(BaseOracleSqlModelPK.class)
public class BaseOracleSqlModel {
    /**
     * 查询返回结果
     * @author Advance
     * @date 2021/10/14 15:27
     * @param null
     * @return null
     */
    @Id
    private String retvalue;


}
