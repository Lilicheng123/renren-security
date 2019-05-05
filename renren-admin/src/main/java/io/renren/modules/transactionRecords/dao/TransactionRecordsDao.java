package io.renren.modules.transactionRecords.dao;

import io.renren.modules.orderInfo.entity.OrderInfoEntity;
import io.renren.modules.transactionRecords.entity.TransactionRecordsEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author llc
 * @email 787254039@qq.com
 * @date 2019-05-02 16:26:52
 */
public interface TransactionRecordsDao extends BaseMapper<TransactionRecordsEntity> {

    Integer selectTotal(@Param("map") Map<String,Object> params);

    List<OrderInfoEntity> queryPageList(@Param("map") Map<String,Object> params);
}
