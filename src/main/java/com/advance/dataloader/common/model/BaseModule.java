package com.advance.dataloader.common.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Advance
 * @date 2021年10月14日 10:40
 * @since V1.0.0
 */
@Data
public class BaseModule implements Serializable {
    private Date createtime;
    private String createname;
}
