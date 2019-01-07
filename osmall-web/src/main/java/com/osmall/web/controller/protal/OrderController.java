package com.osmall.web.controller.protal;

import com.alipay.api.AlipayApiException;

import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.osmall.common.Const;
import com.osmall.common.ResponseCode;
import com.osmall.common.ServiceResponse;
import com.osmall.user.pojo.User;
import com.osmall.product.service.IOrderService;
import com.osmall.util.CookieUtil;
import com.osmall.util.JsonUtil;
import com.osmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Johnson on 2018/4/17.
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

    private static final Logger logger= LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;


    @RequestMapping("create.do")
    @ResponseBody
    public ServiceResponse create(HttpServletRequest httpServletRequest, Integer shippingId){
       // User user=(User)session.getAttribute(Const.CURRENT_USER);
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr= RedisShardedPoolUtil.get(loginToken);
        User user= JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.createOrder(user.getId(),shippingId);
    }

    @RequestMapping("cancel.do")
    @ResponseBody
    public ServiceResponse cancel(HttpServletRequest httpServletRequest, Long orderNo){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user ==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancel(user.getId(),orderNo);
    }


    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServiceResponse getOrderCartProduct(HttpServletRequest httpServletRequest){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user ==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }



    @RequestMapping("detail.do")
    @ResponseBody
    public ServiceResponse detail(HttpServletRequest httpServletRequest, Long orderNo){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user ==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServiceResponse list(HttpServletRequest httpServletRequest, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user ==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
    }



    @RequestMapping("pay.do")
    @ResponseBody
    public ServiceResponse pay(HttpServletRequest httpServletRequest, Long orderNo, HttpServletRequest request){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        String path=request.getSession().getServletContext().getRealPath("upload");//获取项目所在路径中的upload文件路径
        return iOrderService.pay(orderNo,user.getId(),path);
    }


    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public java.lang.Object alipayCallback(HttpServletRequest request){//为什么用java.lang.Object呢？是因为要根据支付宝要求的返回来返回
        //支付宝的回调可以通过request来获取
        Map<String,String> params= Maps.newHashMap();

        Map requestPatams=request.getParameterMap();
        for(Iterator iter=requestPatams.keySet().iterator();iter.hasNext();){
            String name=(String)iter.next();
            String[] values=(String[]) requestPatams.get(name);
            String valueStr="";
            for (int i=0;i<values.length;i++){
                valueStr=(i==values.length-1)?valueStr+values[i]:valueStr+values[i]+",";
            }
            params.put(name,valueStr);
        }
        logger.info("支付宝回掉，sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());


        //非常重要，验证回掉的正确性，是不是支付宝发的，并且还要避免重复通知

        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2= AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if (!alipayRSACheckedV2){
                return ServiceResponse.createByErrorMessage("非法请求，验证不通过，再恶意请求我就找网警了");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常",e);
        }


        //todo 验证各种数据
        ServiceResponse serviceResponse=iOrderService.aliCallback(params);
        if (serviceResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }

        return Const.AlipayCallback.RESPONSE_FAILED;

    }

    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServiceResponse<Boolean> queryOrderPayStatus(HttpServletRequest httpServletRequest, Long orderNo){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user ==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        ServiceResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(),orderNo);
        if(serverResponse.isSuccess()){
            return ServiceResponse.createBySuccess(true);
        }
        return ServiceResponse.createBySuccess(false);
    }

}
