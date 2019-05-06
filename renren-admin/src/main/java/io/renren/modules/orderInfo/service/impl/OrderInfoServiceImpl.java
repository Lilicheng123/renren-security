package io.renren.modules.orderInfo.service.impl;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.renren.common.e.DeleteEnum;
import io.renren.common.utils.OrderInfoUtils;
import io.renren.common.utils.R;
import io.renren.modules.orderInfo.dto.OrderInfoItemDto;
import io.renren.modules.orderItem.entity.OrderItemEntity;
import io.renren.modules.orderItem.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;

import io.renren.modules.orderInfo.dao.OrderInfoDao;
import io.renren.modules.orderInfo.entity.OrderInfoEntity;
import io.renren.modules.orderInfo.service.OrderInfoService;
import org.springframework.transaction.annotation.Transactional;


@Service("orderInfoService")
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoDao, OrderInfoEntity> implements OrderInfoService {

    @Autowired
    private OrderItemService orderItemService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<OrderInfoEntity> page = this.selectPage(
                new Query<OrderInfoEntity>(params).getPage(),
                new EntityWrapper<OrderInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public R queryPageOrderList(Map<String, Object> params) {

        Page<OrderInfoEntity> page = new Page<>(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("limit").toString()));
        Integer current = Integer.parseInt(params.get("page").toString());

        Integer limit = Integer.parseInt(params.get("limit").toString());
        if (current > 0) {
            current--;
        }
        params.put("current", current * limit);
        List<OrderInfoEntity> list = this.baseMapper.queryPageOrderList(params);
        page.setRecords(list);
        Integer total = this.baseMapper.selectOrderTotal(params); //单独查询一遍总数
        page.setTotal(total);
        PageUtils pageUtils = new PageUtils(page);
        return R.ok().put("page", pageUtils);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R saveOrder(OrderInfoItemDto orderInfoItemDto) {
        //先生成订单号

        Boolean flag=true;

        String orderNo="";
       do {
           orderNo = OrderInfoUtils.getOrderNo();
           OrderInfoEntity orderInfoEntity = this.selectById(orderNo);
           if(orderInfoEntity==null){
               flag=false;
           }
       }while(flag);

       //定义一个变量用于接收订单总金额
        BigDecimal payAbleAmount=BigDecimal.ZERO;
       if(orderInfoItemDto.getOrderItemData().size()>0){
           for (OrderItemEntity orderItem: orderInfoItemDto.getOrderItemData()) {
               orderItem.setCreateTime(new Date());
               payAbleAmount=payAbleAmount.add(orderItem.getAmount());
               orderItem.setOrderId(Long.valueOf(orderNo));
               orderItem.setDeleted(DeleteEnum.NOT_DELETE.toInt());
               orderItem.setGoodsImgUrl(orderItem.getGoodsImgUrl().replaceAll("&amp;","&"));
           }

           orderItemService.insertBatch(orderInfoItemDto.getOrderItemData());
       }


        OrderInfoEntity orderInfoEntity=new OrderInfoEntity();
        orderInfoEntity.setOrderNo(Long.valueOf(orderNo));
        orderInfoEntity.setMemberId(1);
        orderInfoEntity.setPayableAmount(payAbleAmount);
        orderInfoEntity.setPayed(BigDecimal.ZERO);
        orderInfoEntity.setOrderCreatetime(new Date());
        orderInfoEntity.setMemberName("admin");
        orderInfoEntity.setPayStatus(0);
        orderInfoEntity.setDiscount(BigDecimal.ZERO);
        orderInfoEntity.setTotalAmount(payAbleAmount);
        this.insert(orderInfoEntity);
        return R.ok().put("orderInfo",orderInfoEntity);
    }

}
