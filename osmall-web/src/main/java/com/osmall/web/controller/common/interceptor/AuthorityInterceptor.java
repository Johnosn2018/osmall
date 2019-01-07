package com.osmall.web.controller.common.interceptor;

import com.osmall.common.Const;
import com.osmall.common.ServiceResponse;
import com.osmall.user.pojo.User;
import com.osmall.util.CookieUtil;
import com.osmall.util.JsonUtil;
import com.osmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor{
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        log.info("preHandle");
        //请求中Comtroller中的方法名
        HandlerMethod handlerMethod=(HandlerMethod)handler;

        //解析HandlerMethod
        String methodName=handlerMethod.getMethod().getName();
        String className=handlerMethod.getBean().getClass().getSimpleName();

        //解析参数，具体的参数key以及value是什么，打印日志
        StringBuffer requestParamBuffer=new StringBuffer();
        Map paramMap=httpServletRequest.getParameterMap();
        Iterator it=paramMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry entry=(Map.Entry)it.next();
            String mapKey=(String)entry.getKey();

            String mapValue= StringUtils.EMPTY;
            //request这个参数的map,里面的value返回的是一个String[]
            Object obj=entry.getValue();
            if(obj instanceof String[]){
                String[] strs=(String[])obj;
                mapValue= Arrays.toString(strs);
            }
            requestParamBuffer.append(mapKey).append("=").append(mapValue);
        }

        if(StringUtils.equals(className,"UserManageController")&&StringUtils.equals(methodName,"login")){
            log.info("权限拦截器拦截到请求，className:{},methodName:{}",className,methodName);
            //如果是拦截到登录请求，不打印参数，因为参数里面有密码
            return true;
        }

        User user=null;
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isNotEmpty(loginToken)){
            String userJsonStr= RedisShardedPoolUtil.get(loginToken);
            user= JsonUtil.string2Obj(userJsonStr,User.class);
        }

        if(user == null || (user.getRole().intValue()!= Const.Role.ROLE_ADMIN)){
            //返回false,即不会调用Controller里的方法
            httpServletResponse.reset();//要添加reset，否则回报异常
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            PrintWriter out=httpServletResponse.getWriter();
            if(user==null){
                out.print(JsonUtil.object2String(ServiceResponse.createByErrorMessage("拦截器拦截，用户未登录")));
            }else{
                out.print(JsonUtil.object2String(ServiceResponse.createByErrorMessage("拦截器拦截，用户无权限登录")));
            }
            out.flush();
            out.close();
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        log.info("afterCompletion");
    }
}
