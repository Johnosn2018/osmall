package com.osmall.web.controller.backend;

import com.osmall.common.Const;
import com.osmall.common.ResponseCode;
import com.osmall.common.ServiceResponse;
import com.osmall.user.pojo.User;
import com.osmall.user.service.IUserService;
import com.osmall.util.CookieUtil;
import com.osmall.util.JsonUtil;
import com.osmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by Johnson on 2018/4/2.
 */
@Controller
@RequestMapping("/manage/user")
public class UserManageController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value="login.do",method= RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse){
        ServiceResponse<User> response=iUserService.login(username,password);
        if(response.isSuccess()){
            User user=response.getData();
            if(user.getRole()== Const.Role.ROLE_ADMIN){
                //说明登录的是管理员
               // session.setAttribute(Const.CURRENT_USER,user);
                CookieUtil.writeLoginToken(httpServletResponse,session.getId());
                RedisShardedPoolUtil.setEx(session.getId(),JsonUtil.object2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
                return response;
            }else{
                return ServiceResponse.createByErrorMessage("不是管理员，无法登录");//
            }
        }
        return response;
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServiceResponse getList(HttpServletRequest httpServletRequest, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize ){
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
            return  iUserService.getUserList(pageNum,pageSize);
        }else{
            return ServiceResponse.createByErrorMessage("无权限操作");
        }
    }



}
