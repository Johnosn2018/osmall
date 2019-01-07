package com.osmall.web.controller.backend;

import com.osmall.common.ResponseCode;
import com.osmall.common.ServiceResponse;
import com.osmall.user.pojo.User;
import com.osmall.user.service.IUserService;
import com.osmall.util.CookieUtil;
import com.osmall.util.JsonUtil;
import com.osmall.util.RedisShardedPoolUtil;
import com.osmall.user.vo.UserStatisticVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Johnson on 2018/8/11.
 */
@Controller
@RequestMapping("/manage/statistic")
public class UserManagerStatisticController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value="base_count.do",method= RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<UserStatisticVo> base_count(HttpServletRequest httpServletRequest){
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr= RedisShardedPoolUtil.get(loginToken);
        User user= JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //return  iUserService.getUserList(pageNum,pageSize);
           // return iUserService.getStatistic();
            return ServiceResponse.createByErrorMessage("待完成UserManagerStatisticController");
        }else{
            return ServiceResponse.createByErrorMessage("无权限操作");
        }

    }

}
