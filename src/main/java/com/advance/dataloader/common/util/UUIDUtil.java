package com.advance.dataloader.common.util;

import java.util.UUID;

/**
 * @author Advance
 * @date 2021年11月09日 22:10
 * @since V1.0.0
 */
public class UUIDUtil {
    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

}
