package com.advance.dataloader.common.base;

import org.springframework.stereotype.Component;

/**
 * 原子变量类
 * @author Advance
 * @date 2021年11月13日 18:22
 * @since V1.0.0
 */
@Component
public class AtomicVariable {
    /**
     * 执行标记
     */
    public volatile boolean runSuccess = false;

    public boolean isRunSuccess() {
        return runSuccess;
    }

    public void setRunSuccess(boolean runSuccess) {
        this.runSuccess = runSuccess;
    }
}
