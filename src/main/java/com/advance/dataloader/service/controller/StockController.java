package com.advance.dataloader.service.controller;

import com.advance.dataloader.common.base.AtomicVariable;
import com.advance.dataloader.common.http.APIResponse;
import com.advance.dataloader.config.DataLoaderConfiguration;
import com.advance.dataloader.repo.sqlloader.DrSqlLoaderLog;
import com.advance.dataloader.repo.sqlloader.SqlLoaderRepository;
import com.advance.dataloader.sqlloader.RunnerApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

/**
 * @author Advance
 * @date 2021年11月13日 11:33
 * @since V1.0.0
 */
@RestController//声明基于rest的控制器
@RequestMapping("/stock")//根url配置
public class StockController {
    @Autowired//自动注入
    private SqlLoaderRepository sqlLoaderRepository;
    @Autowired
    private RunnerApplication runner;
    @Autowired
    private DataLoaderConfiguration dataLoaderConfiguration;
    @Autowired
    private AtomicVariable atomicVariable;

    /**
     * 获取所有数据
     * @author Advance
     * @date 2021/11/13 21:13
     * @param page
     * @param size
     * @return org.springframework.data.domain.Page<com.advance.dataloader.repo.sqlloader.DrSqlLoaderLog>
     */
    @GetMapping("/findAll/{page}/{size}")//get请求所有数据
    public Page<DrSqlLoaderLog> findAll(@PathVariable("page")Integer page, @PathVariable("size")Integer size){
        PageRequest pageRequest = PageRequest.of(page, size);
        return sqlLoaderRepository.findAll(pageRequest);
    }


    /**
     * 执行脚本
     * @author Advance
     * @date 2021/11/13 21:13
     * @return com.advance.dataloader.common.http.APIResponse
     */
    @PostMapping("/runScript")
    public APIResponse runScript(){
        try {
            if(atomicVariable.isRunSuccess()){
                return new APIResponse(0); //失败
            }
            atomicVariable.setRunSuccess(true);
            dataLoaderConfiguration.setRunScript(true); //设置可运行
            runner.run(new DefaultApplicationArguments());
            return new APIResponse(1); //成功
        } catch (Exception e) {
            atomicVariable.setRunSuccess(false);
            e.printStackTrace();
        }
        return new APIResponse(1); //成功
    }


    /**
     * 根据Id查询唯一记录
     * @author Advance
     * @date 2021/11/13 21:02
     * @param serialid
     * @return com.advance.dataloader.repo.sqlloader.DrSqlLoaderLog
     */
    @GetMapping(value = "/findById/{serialid}")
    public DrSqlLoaderLog findById(@PathVariable("serialid") String  serialid){
        DrSqlLoaderLog drSqlLoaderLog = sqlLoaderRepository.selectById(serialid);
        return drSqlLoaderLog;
    }

    /**
     * 更新
     * @author Advance
     * @date 2021/11/13 21:01
     * @param log
     * @return com.advance.dataloader.common.http.APIResponse
     */
    @PutMapping(value = "/update")
    public APIResponse update(@RequestBody DrSqlLoaderLog log){
        DrSqlLoaderLog save = sqlLoaderRepository.save(log);
        return new APIResponse(save);
    }

}
