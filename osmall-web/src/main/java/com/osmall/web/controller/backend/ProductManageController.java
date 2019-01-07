package com.osmall.web.controller.backend;

import com.google.common.collect.Maps;
import com.osmall.common.ResponseCode;
import com.osmall.common.ServiceResponse;
import com.osmall.product.pojo.Product;
import com.osmall.user.pojo.User;
import com.osmall.product.service.IFileService;
import com.osmall.product.service.IProductService;
import com.osmall.user.service.IUserService;
import com.osmall.util.CookieUtil;
import com.osmall.util.JsonUtil;
import com.osmall.util.PropertiesUtil;
import com.osmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Johnson on 2018/4/8.
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {//

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServiceResponse productSave(HttpServletRequest httpServletRequest, Product product){
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
            //填充我们增加业务逻辑
           return iProductService.saveOrUpdateProduct(product);
        }else{
            return ServiceResponse.createByErrorMessage("无权限操作");
        }
    }


    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServiceResponse setSaleStatus(HttpServletRequest httpServletRequest, Integer productId, Integer status){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.setSaleStatus(productId,status);
        }else{
            return ServiceResponse.createByErrorMessage("无权限操作");
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServiceResponse getDetail(HttpServletRequest httpServletRequest, Integer productId){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            //填充业务
            return iProductService.manageProductDetail(productId);
        }else{
            return ServiceResponse.createByErrorMessage("无权限操作");
        }
    }


    @RequestMapping("list.do")
    @ResponseBody
    public ServiceResponse getList(HttpServletRequest httpServletRequest, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize ){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.getProductList(pageNum,pageSize);
        }else{
            return ServiceResponse.createByErrorMessage("无权限操作");
        }
    }


    @RequestMapping("search.do")
    @ResponseBody
    public ServiceResponse productSearch(HttpServletRequest httpServletRequest, String productName, Integer productId, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize ){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }else{
            return ServiceResponse.createByErrorMessage("无权限操作");
        }
    }

    @RequestMapping("upload.do")
    @ResponseBody
    //upload_file对应前端form标签中的input标签的名字
    public ServiceResponse upload(HttpServletRequest httpServletRequest, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request){
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServiceResponse.createByErrorMessage("用户未登录，无法获取当前用户的信息");
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录管理员");
        }

        if(iUserService.checkAdminRole(user).isSuccess()){
            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFileName=iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

            Map fileMap= Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServiceResponse.createBySuccess(fileMap);
        }else{
            return ServiceResponse.createByErrorMessage("无权限操作");
        }
    }


    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpServletRequest httpServletRequest, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap=Maps.newHashMap();
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }

        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }

        //富文本中对于返回值有自己的要求，我们使用的是simditor所以按照simditor的要求进行返回

        if(iUserService.checkAdminRole(user).isSuccess()){
            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFileName=iFileService.upload(file,path);
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            response.addHeader("Access_Control-Allow-Headers","X-File-Name");
            return resultMap;
        }else{
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }
    }

}
