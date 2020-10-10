package com.offcn.webui.controller;

import com.offcn.dycommon.response.AppResponse;
import com.offcn.webui.service.OrderServiceFeign;
import com.offcn.webui.vo.resp.OrderFormInfoSubmitVo;
import com.offcn.webui.vo.resp.ReturnPayConfirmVo;
import com.offcn.webui.vo.resp.TOrder;
import com.offcn.webui.vo.resp.UserRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderServiceFeign orderServiceFeign;

    /**
     * 保存订单
     * @param vo
     * @param session
     * @return
     */
    @RequestMapping("/save")
    public String orderPay(OrderFormInfoSubmitVo vo, HttpSession session){

        //判断是否登录
        UserRespVo respVo = (UserRespVo) session.getAttribute("sessionMember");
        if (respVo == null){
            session.setAttribute("preUrl","order/save");
            return "redirect:/login";
        }
        String accessToken = respVo.getAccessToken();
        vo.setAccessToken(accessToken);

        ReturnPayConfirmVo payConfirmVo = (ReturnPayConfirmVo) session.getAttribute("returnConfirmSession");
        if (payConfirmVo == null){
            return "redirect:/login";
        }
        vo.setProjectid(payConfirmVo.getProjectId());
        vo.setReturnid(payConfirmVo.getId());
        vo.setRtncount(payConfirmVo.getNum());

        AppResponse<TOrder> tOrderAppResponse = orderServiceFeign.saveOrder(vo);
        TOrder tOrder = tOrderAppResponse.getData();

        //下单成功，打印相关信息待处理
        String orderName = payConfirmVo.getProjectName();
        System.out.println("orderNum:"+tOrder.getOrdernum());
        System.out.println("money:"+tOrder.getMoney());
        System.out.println("orderName:"+orderName);
        System.out.println("Remark:"+vo.getRemark());

        return "/member/minecrowdfunding";
    }
}
