package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.exception.XxlJobException;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.self.XxlJobInfoVO;
import com.xxl.job.admin.core.thread.JobTriggerPoolHelper;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.core.util.JacksonUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.XxlJobRemotingUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 自定义api
 *
 * @author liuhan 2020/5/2 12:42
 */
@Controller
@RequestMapping("/api/self")
public class JobApiSelfController {

    @Resource
    private AdminBiz adminBiz;
    @Resource
    XxlJobGroupDao xxlJobGroupDao;
    @Resource
    private XxlJobService xxlJobService;



    // ---------------------- base ----------------------

    /**
     * valid access token
     */
    private void validAccessToken(HttpServletRequest request){
        if (XxlJobAdminConfig.getAdminConfig().getAccessToken()!=null
                && XxlJobAdminConfig.getAdminConfig().getAccessToken().trim().length()>0
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().equals(request.getHeader(XxlJobRemotingUtil.XXL_RPC_ACCESS_TOKEN))) {
            throw new XxlJobException("The access token is wrong.");
        }
    }

    /**
     * parse Param
     */
    private Object parseParam(String data, Class<?> parametrized, Class<?>... parameterClasses){
        Object param = null;
        try {
            if (parameterClasses != null) {
                param = JacksonUtil.readValue(data, parametrized, parameterClasses);
            } else {
                param = JacksonUtil.readValue(data, parametrized);
            }
        } catch (Exception e) { }
        if (param==null) {
            throw new XxlJobException("The request data invalid.");
        }
        return param;
    }

    // ---------------------- admin biz ----------------------


    /**
     * 添加任务
     *
     * @param xxlJobInfoVO
     * @return
     */
    @PostMapping("/add-task")
    @ResponseBody
    @PermissionLimit(limit=false)
    public ReturnT<String> registry(HttpServletRequest request, @RequestBody XxlJobInfoVO xxlJobInfoVO) {
        // valid
        validAccessToken(request);
        // 为了最小改动原作者代码，直接在controller操作dao了
        String executorName = xxlJobInfoVO.getExecutorName();
        XxlJobGroup xxlJobGroup = xxlJobGroupDao.selectByAppName(executorName);
        if (Objects.isNull(xxlJobGroup)){
            return ReturnT.FAIL;
        }
        xxlJobInfoVO.setJobGroup(xxlJobGroup.getId());
        return xxlJobService.add(xxlJobInfoVO);
    }

    @RequestMapping("/update")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> update(HttpServletRequest request,@RequestBody XxlJobInfoVO xxlJobInfoVO) {
        // valid
        validAccessToken(request);
        // 为了最小改动原作者代码，直接在controller操作dao了
        String executorName = xxlJobInfoVO.getExecutorName();
        XxlJobGroup xxlJobGroup = xxlJobGroupDao.selectByAppName(executorName);
        if (Objects.isNull(xxlJobGroup)){
            return ReturnT.FAIL;
        }
        xxlJobInfoVO.setJobGroup(xxlJobGroup.getId());
        return xxlJobService.update(xxlJobInfoVO);
    }

    /**
     * 删除任务
     * @param request
     * @param id
     * @return
     */
    @RequestMapping("/remove")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> remove(HttpServletRequest request, int id) {
        // valid
        validAccessToken(request);
        return xxlJobService.remove(id);
    }

    @RequestMapping("/stop")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> pause(HttpServletRequest request, int id) {
        // valid
        validAccessToken(request);
        return xxlJobService.stop(id);
    }

    @RequestMapping("/start")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> start(HttpServletRequest request, int id) {
        // valid
        validAccessToken(request);
        return xxlJobService.start(id);
    }

    /**
     * 触发任务
     *
     * @param request
     * @param id
     * @param executorParam
     * @return
     */
    @GetMapping("/trigger")
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> triggerJob(HttpServletRequest request, int id, String executorParam) {
        // valid
        validAccessToken(request);

        // force cover job param
        if (executorParam == null) {
            executorParam = "";
        }

        JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam);
        return ReturnT.SUCCESS;
    }

}
