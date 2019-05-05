package io.renren.modules.transactionRecords.controller;

import java.util.Arrays;
import java.util.Map;

import io.renren.common.validator.ValidatorUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.renren.modules.transactionRecords.entity.TransactionRecordsEntity;
import io.renren.modules.transactionRecords.service.TransactionRecordsService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * 
 *
 * @author llc
 * @email 787254039@qq.com
 * @date 2019-05-02 16:26:52
 */
@RestController
@RequestMapping("transactionRecords/transactionrecords")
public class TransactionRecordsController {
    @Autowired
    private TransactionRecordsService transactionRecordsService;

    /**
     * 列表
     */
    @PostMapping("/list")
    @RequiresPermissions("transactionRecords:transactionrecords:list")
    public R list(@RequestBody Map<String, Object> params){
        return transactionRecordsService.queryPage(params);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("transactionRecords:transactionrecords:info")
    public R info(@PathVariable("id") Integer id){
        TransactionRecordsEntity transactionRecords = transactionRecordsService.selectById(id);

        return R.ok().put("transactionRecords", transactionRecords);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("transactionRecords:transactionrecords:save")
    public R save(@RequestBody TransactionRecordsEntity transactionRecords){
        transactionRecordsService.insert(transactionRecords);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("transactionRecords:transactionrecords:update")
    public R update(@RequestBody TransactionRecordsEntity transactionRecords){
        ValidatorUtils.validateEntity(transactionRecords);
        transactionRecordsService.updateAllColumnById(transactionRecords);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("transactionRecords:transactionrecords:delete")
    public R delete(@RequestBody Integer[] ids){
        transactionRecordsService.deleteBatchIds(Arrays.asList(ids));

        return R.ok();
    }

}
