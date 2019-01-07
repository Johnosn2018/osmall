package com.osmall.web.controller.protal;

import com.osmall.common.Const;
import com.osmall.common.ResponseCode;
import com.osmall.common.ServiceResponse;
import com.osmall.user.pojo.User;
import com.osmall.product.service.ICartService;
import com.osmall.util.CookieUtil;
import com.osmall.util.JsonUtil;
import com.osmall.util.RedisShardedPoolUtil;
import com.osmall.product.vo.CartVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Johnson on 2018/4/15.
 */
@Controller
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    private ICartService iCartService;

    @RequestMapping("list.do")//查询
    @ResponseBody
    public ServiceResponse<CartVo> list(HttpServletRequest httpServletRequest){
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr= RedisShardedPoolUtil.get(loginToken);
        User user= JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());
    }

    @RequestMapping("add.do")
    @ResponseBody
    public ServiceResponse<CartVo> add(HttpServletRequest httpServletRequest, Integer count, Integer productId){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(user.getId(),productId,count);
    }


    @RequestMapping("update.do")
    @ResponseBody
    public ServiceResponse<CartVo> update(HttpServletRequest httpServletRequest, Integer count, Integer productId){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.update(user.getId(),productId,count);
    }


    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServiceResponse<CartVo> deleteProduct(HttpServletRequest httpServletRequest, String  productIds){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProduct(user.getId(),productIds);
    }

    @RequestMapping("select_all.do")
    @ResponseBody
    public ServiceResponse<CartVo> selectAll(HttpServletRequest httpServletRequest){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.CHECKED);
    }

    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServiceResponse<CartVo> unSelectAll(HttpServletRequest httpServletRequest){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.UN_CHECKED);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServiceResponse<CartVo> Select(HttpServletRequest httpServletRequest, Integer productId){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.CHECKED);
    }

    @RequestMapping("un_select.do")
    @ResponseBody
    public ServiceResponse<CartVo> unSelect(HttpServletRequest httpServletRequest, Integer productId){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.UN_CHECKED);
    }


    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServiceResponse<Integer> getCartProductCount(HttpServletRequest httpServletRequest){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }


    //全选
   // 全反选

    //单独选
    //单独反选

    //查询当前用户的购物车里面的产品数量，如果一个产品有10个，那么数量就是10


}
