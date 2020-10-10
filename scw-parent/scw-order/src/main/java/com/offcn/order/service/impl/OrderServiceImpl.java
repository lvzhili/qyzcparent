package com.offcn.order.service.impl;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.order.enums.OrderStatusEnumes;
import com.offcn.order.mapper.TOrderMapper;
import com.offcn.order.po.TOrder;
import com.offcn.order.service.OrderService;
import com.offcn.order.service.ProjectServiceFeign;
import com.offcn.order.vo.req.OrderInfoSubmitVo;
import com.offcn.order.vo.resp.TReturn;
import com.offcn.util.AppDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private TOrderMapper orderMapper;

    @Autowired
    private ProjectServiceFeign projectServiceFeign;

    @Override
    public TOrder saveOrder(OrderInfoSubmitVo vo) {
        TOrder order = new TOrder();
        //用户获取令牌
        String accessToken = vo.getAccessToken();
        //通过令牌获取用户id
        String mid = stringRedisTemplate.opsForValue().get(accessToken);
        //远程调用通过pid获取回报信息
        AppResponse<List<TReturn>> listAppResponse = projectServiceFeign.getReturn(vo.getProjectid());
        List<TReturn> returnList = listAppResponse.getData();
        TReturn tReturn = returnList.get(0);
        //将信息存储到order中
        order.setMemberid(Integer.parseInt(mid));
        order.setProjectid(vo.getProjectid());
        order.setReturnid(vo.getReturnid());
        order.setCreatedate(AppDateUtils.getFormatTime());
        String orderNum= UUID.randomUUID().toString().replace("-", "");
        order.setOrdernum(orderNum);

        //计算汇报金额 汇报数量*支持金额 + 运费
        Integer totleMoney = vo.getRtncount()*tReturn.getSupportmoney() + tReturn.getFreight();
        order.setMoney(totleMoney);
        //回报数量
        order.setRtncount(vo.getRtncount());
        //状态
        order.setStatus(OrderStatusEnumes.UNPAY.getCode()+"");
        //地址
        order.setAddress(vo.getAddress());
        //是否开发票
        order.setInvoice(vo.getInvoice().toString());
        //发票头
        order.setInvoictitle(vo.getInvoictitle());
        //描述
        order.setRemark(vo.getRemark());
        //添加
        orderMapper.insertSelective(order);
        return order;
    }
}
