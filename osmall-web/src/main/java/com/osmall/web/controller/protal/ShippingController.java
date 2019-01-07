package com.osmall.web.controller.protal;

import com.github.pagehelper.PageInfo;
import com.osmall.common.ResponseCode;
import com.osmall.common.ServiceResponse;
import com.osmall.product.pojo.Shipping;
import com.osmall.user.pojo.User;
import com.osmall.product.service.IShippingService;
import com.osmall.util.CookieUtil;
import com.osmall.util.JsonUtil;
import com.osmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Johnson on 2018/4/16.
 */
@Controller
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;


    @RequestMapping("add.do")
    @ResponseBody
    public ServiceResponse add(HttpServletRequest httpServletRequest, Shipping shipping){
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr= RedisShardedPoolUtil.get(loginToken);
        User user= JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.add(user.getId(),shipping);
    }


    @RequestMapping("del.do")
    @ResponseBody
    public ServiceResponse del(HttpServletRequest httpServletRequest, Integer shippingId){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.del(user.getId(),shippingId);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServiceResponse update(HttpServletRequest httpServletRequest, Shipping shipping){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.update(user.getId(),shipping);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServiceResponse<Shipping> select(HttpServletRequest httpServletRequest, Integer shippingId){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.select(user.getId(),shippingId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServiceResponse<PageInfo> list(@RequestParam(value = "pageNum",defaultValue = "1")int pageNum, @RequestParam(value = "pageSize",defaultValue = "10")int pageSize, HttpServletRequest httpServletRequest){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        return iShippingService.list(user.getId(),pageNum,pageSize);
    }
}
