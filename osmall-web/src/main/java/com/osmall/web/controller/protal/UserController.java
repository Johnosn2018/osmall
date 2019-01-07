package com.osmall.web.controller.protal;

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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by Johnson on 2018/4/1.
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value="login.do",method= RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest){
        //service->mybatis->dao
        ServiceResponse<User> response=iUserService.login(username,password);//
        if(response.isSuccess()){
           //session.setAttribute(Const.CURRENT_USER,response.getData());
            CookieUtil.writeLoginToken(httpServletResponse,session.getId());
            //CookieUtil.readLoginToken(httpServletRequest);
            //CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
            RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.object2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);

        }
        return response;
    }


    @RequestMapping(value="logout.do",method= RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        //session.removeAttribute(Const.CURRENT_USER);

        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
        RedisShardedPoolUtil.del(loginToken);
        return ServiceResponse.createBySuccess();
    }


    @RequestMapping(value="register.do",method= RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> register(User user){
        return iUserService.register(user);
    }


    @RequestMapping(value="check_valid.do",method= RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    @RequestMapping(value="get_user_info.do",method= RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> getUserInfo(HttpServletRequest httpServletRequest){
       // User user=(User)session.getAttribute(Const.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);

        if(user!=null){
            return ServiceResponse.createBySuccess(user);
        }
        return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
    }

    @RequestMapping(value="forget_get_question.do",method= RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    @RequestMapping(value="forget_check_answer.do",method= RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }

    @RequestMapping(value="forget_reset_password.do",method= RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> forgetRegistPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetRegistPassword(username,passwordNew,forgetToken);
    }

    @RequestMapping(value="reset_password.do",method= RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> resetPassword(HttpServletRequest httpServletRequest, String passwordOld, String passwordNew){
       // User user=(User)session.getAttribute(Const.CURRENT_USER);
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.resetPassword(passwordOld,passwordNew,user);
    }

    @RequestMapping(value="update_information.do",method= RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> update_information(HttpServletRequest httpServletRequest, User user){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User currentUser=JsonUtil.string2Obj(userJsonStr,User.class);
        if(currentUser==null){
            return ServiceResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServiceResponse<User> response=iUserService.updateInformation(user);
        if(response.isSuccess()){
            response.getData().setUsername(currentUser.getUsername());
            RedisShardedPoolUtil.setEx(loginToken, JsonUtil.object2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }


    @RequestMapping(value="get_information.do",method= RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> get_information(HttpServletRequest httpServletRequest){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }
        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User currentUser=JsonUtil.string2Obj(userJsonStr,User.class);
        if(currentUser==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登录，需要强制登录status=10");
        }
        return iUserService.getInformation(currentUser.getId());
    }
}
