package io.renren.modules.transactionRecords.service;

import com.baomidou.mybatisplus.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.transactionRecords.entity.TransactionRecordsEntity;

import java.util.Map;

/**
 * 
 *
 * @author llc
 * @email 787254039@qq.com
 * @date 2019-05-02 16:26:52
 */
public interface TransactionRecordsService extends IService<TransactionRecordsEntity> {

    R queryPage(Map<String, Object> params);
}

