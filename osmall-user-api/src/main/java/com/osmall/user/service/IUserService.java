package com.osmall.user.service;

import com.github.pagehelper.PageInfo;
import com.osmall.common.ServiceResponse;
import com.osmall.user.pojo.User;
import com.osmall.user.vo.UserStatisticVo;
/**
 * Created by Johnson on 2018/4/1.
 */
public interface IUserService {

    ServiceResponse<User> login(String username, String password);

    ServiceResponse<String> register(User user);

    ServiceResponse<String> checkValid(String str,String type);

    ServiceResponse selectQuestion(String username);

    ServiceResponse<String> checkAnswer(String username,String question,String answer);

    ServiceResponse<String> forgetRegistPassword(String username,String passwordNew,String forgetToken);

    ServiceResponse<String> resetPassword(String passwordOld,String passwordNew,User user);

    ServiceResponse<User> updateInformation(User user);

    ServiceResponse<User> getInformation(Integer userId);

    ServiceResponse checkAdminRole(User user);//

    //获取用户列表
    ServiceResponse<PageInfo> getUserList(int pageNum, int pageSize);

    //获取统计用户、商品、订单数量
    //ServiceResponse<UserStatisticVo> getStatistic();

}
