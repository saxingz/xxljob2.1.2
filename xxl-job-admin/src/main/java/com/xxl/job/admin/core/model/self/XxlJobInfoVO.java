package com.xxl.job.admin.core.model.self;

import com.xxl.job.admin.core.model.XxlJobInfo;

/**
 * 开次开发类， 动态创建任务，增加名称
 * 
 * @author liuhan 2020/5/2 13:21
 */
public class XxlJobInfoVO extends XxlJobInfo {

    private String executorName;

    public String getExecutorName() {
        return executorName;
    }

    public void setExecutorName(String executorName) {
        this.executorName = executorName;
    }
}
