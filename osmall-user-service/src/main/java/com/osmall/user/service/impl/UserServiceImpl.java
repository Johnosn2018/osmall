package com.osmall.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.osmall.common.Const;
import com.osmall.common.ServiceResponse;
//import com.osmall.product.mapper.ProductMapper;
import com.osmall.user.mapper.UserMapper;
import com.osmall.user.pojo.User;
import com.osmall.user.service.IUserService;
import com.osmall.util.MD5Util;
import com.osmall.util.RedisShardedPoolUtil;
import com.osmall.user.vo.UserListVo;
import com.osmall.user.vo.UserStatisticVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Created by Johnson on 2018/4/1.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    //@Autowired
   // private ProductMapper productMapper;

   // @Autowired
    //private OrderMapper orderMapper;

    @Override
    public ServiceResponse<User> login(String username, String password) {

        int resultCount =userMapper.checkUsername(username);
        if(resultCount==0){
            return ServiceResponse.createByErrorMessage("用户名不存在");
        }

        //todo 密码登录md5
        String md5Password=MD5Util.MD5EncodeUtf8(password);
        User user=userMapper.selectLogin(username,md5Password);
        if(user==null){
            return ServiceResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySuccess("登录成功",user);

    }


    public ServiceResponse<String> register(User user){
        ServiceResponse valudResponse=this.checkValid(user.getUsername(),Const.USERNAME);
        if(!valudResponse.isSuccess()){
            return valudResponse;
        }
        valudResponse=this.checkValid(user.getEmail(),Const.EMAIL);
        if(!valudResponse.isSuccess()){
            return valudResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount=userMapper.insert(user);
        if(resultCount==0){
            return ServiceResponse.createByErrorMessage("注册失败");
        }
        return ServiceResponse.createBySuccessMessage("注册成功");

    }

    public ServiceResponse<String> checkValid(String str,String type){
        if(org.apache.commons.lang3.StringUtils.isNotBlank(type)){
            //开始校验
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0 ){
                    return ServiceResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0 ){
                    return ServiceResponse.createByErrorMessage("email已存在");
                }
            }
        }else{
            return ServiceResponse.createByErrorMessage("参数错误");
        }
        return ServiceResponse.createBySuccessMessage("校验成功");
    }

    public ServiceResponse selectQuestion(String username){
        ServiceResponse validResponse=this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServiceResponse.createBySuccessMessage("用户不存在");
        }
        String question=userMapper.selectQuestionByUsername(username);
        if(org.apache.commons.lang3.StringUtils.isNoneBlank(question)){
            return ServiceResponse.createBySuccess(question);
        }
        return ServiceResponse.createByErrorMessage("找回密码的问题是空的");
    }

    public ServiceResponse<String> checkAnswer(String username,String question,String answer){
        int resultCount=userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
            //说明问题及问题答案是这个用户的，并且正确
            String forgetToken= UUID.randomUUID().toString();
            RedisShardedPoolUtil.setEx(Const.TOKEN_PREFIX+username,forgetToken,60*60*2);
            return ServiceResponse.createBySuccess(forgetToken);
        }
        return ServiceResponse.createByErrorMessage("问题的答案错误");
    }

    public ServiceResponse<String> forgetRegistPassword(String username,String passwordNew,String forgetToken){

        if(org.apache.commons.lang3.StringUtils.isBlank(forgetToken)){
            return ServiceResponse.createByErrorMessage("参数错误，token需要传递");
        }
        ServiceResponse validResponse=this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户不存在
            return ServiceResponse.createBySuccessMessage("用户不存在");
        }

        String token=RedisShardedPoolUtil.get(Const.TOKEN_PREFIX+username);
        if(org.apache.commons.lang3.StringUtils.isBlank(token)){
            return ServiceResponse.createByErrorMessage("token无效或者过期");
        }
        if(org.apache.commons.lang3.StringUtils.equals(forgetToken,token)){
            String md5Password=MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount=userMapper.updatePasswordByUsername(username,md5Password);
            if(rowCount>0){
                return ServiceResponse.createBySuccessMessage("修改密码成功");
            }
        }else{
            return ServiceResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }
        return ServiceResponse.createByErrorMessage("修改密码失败");
    }

    public ServiceResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
        //防止横向越权，要校验一下这个用户的旧密码，一定要指定这个用户,因为我们查询一个count(1),如果不指定id，那么结果就是true,count>0
        int resultCount=userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount==0){
            return ServiceResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount=userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return ServiceResponse.createBySuccessMessage("密码更新成功");
        }
        return ServiceResponse.createByErrorMessage("密码更新失败");
    }

    public ServiceResponse<User> updateInformation(User user){
        //username是不能被更新的
        //email也要进行一个校验，校验新的email是不是已经存在，并且存在的email如果相同的话，不能是我们当前的这个用户的。
        int resultCount=userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount>0){
            return ServiceResponse.createByErrorMessage("email已存在，请更换email再尝试更新");
        }
        User updateUser=new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount=userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount>0){
            return ServiceResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServiceResponse.createByErrorMessage("更新个人信息失败");
    }

    public ServiceResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user==null){
            return ServiceResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createBySuccess(user);
    }

    //backend
    /*
    校验是否是管理员
     */
    public ServiceResponse checkAdminRole(User user){//
        if(user!=null &&user.getRole().intValue()==Const.Role.ROLE_ADMIN){
            return ServiceResponse.createBySuccess();
        }
        return ServiceResponse.createByError();
    }

    //获取用户列表
    public  ServiceResponse<PageInfo> getUserList(int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<User> userList=userMapper.selectUserList();

        List<UserListVo> userListVoList= Lists.newArrayList();
        for(User userItem : userList){
            UserListVo userListVo=assembleUserListVo(userItem);
            userListVoList.add(userListVo);
        }
        PageInfo pageResult=new PageInfo(userList);
        pageResult.setList(userListVoList);
        return ServiceResponse.createBySuccess(pageResult);
    }

    private UserListVo assembleUserListVo(User user){
        UserListVo userListVo=new UserListVo();
        userListVo.setId(user.getId());
        userListVo.setUsername(user.getUsername());
        userListVo.setPassword(user.getPassword());
        userListVo.setEmail(user.getEmail());
        userListVo.setPhone(user.getPhone());
        userListVo.setQuestion(user.getQuestion());
        userListVo.setAnswer(user.getAnswer());
        userListVo.setRole(user.getRole());
        userListVo.setCreateTime(user.getCreateTime());
        userListVo.setUpdateTime(user.getUpdateTime());

        return userListVo;
    }

    //获取统计用户、商品、订单数量
   /* public ServiceResponse<UserStatisticVo> getStatistic(){

        Long userCount=userMapper.getUerCount();

        Long productCount=productMapper.getProductCount();

        Long orderCount=orderMapper.getOrderCount();

        UserStatisticVo userStatisticVo=new UserStatisticVo(userCount,productCount,orderCount);

        return ServiceResponse.createBySuccess(userStatisticVo);
    }*/

}
