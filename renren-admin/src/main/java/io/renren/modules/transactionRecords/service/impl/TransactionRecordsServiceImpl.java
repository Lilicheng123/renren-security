package io.renren.modules.transactionRecords.service.impl;

import io.renren.common.utils.R;
import io.renren.modules.orderInfo.entity.OrderInfoEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import io.renren.modules.transactionRecords.dao.TransactionRecordsDao;
import io.renren.modules.transactionRecords.entity.TransactionRecordsEntity;
import io.renren.modules.transactionRecords.service.TransactionRecordsService;


@Service("transactionRecordsService")
public class TransactionRecordsServiceImpl extends ServiceImpl<TransactionRecordsDao, TransactionRecordsEntity> implements TransactionRecordsService {

    @Override
    public R queryPage(Map<String, Object> params) {
        Page<OrderInfoEntity> page = new Page<>(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        Integer current = Integer.parseInt(params.get("page").toString());

        Integer limit = Integer.parseInt(params.get("limit").toString());
        if (current > 0) {
            current--;
        }
        params.put("current", current * limit);
        List<OrderInfoEntity> list = this.baseMapper.queryPageList(params);
        page.setRecords(list);
        Integer total = this.baseMapper.selectTotal(params); //单独查询一遍总数
        page.setTotal(total);
        PageUtils pageUtils = new PageUtils(page);
        return R.ok().put("page", pageUtils);
    }

}
