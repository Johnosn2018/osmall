package com.osmall.product.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.osmall.common.ServiceResponse;
import com.osmall.product.mapper.ShippingMapper;
import com.osmall.product.pojo.Shipping;
import com.osmall.product.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Johnson on 2018/4/16.
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    public ServiceResponse add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount=shippingMapper.insert(shipping);
        if(rowCount>0){
            Map result= Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return ServiceResponse.createBySuccess("新建地址成功",result);
        }
        return ServiceResponse.createByErrorMessage("新建地址失败");

    }


    public ServiceResponse<String> del(Integer userId,Integer shippingId ){
        int resultCount=shippingMapper.deleteByShippingIdUserId(userId,shippingId);
        if (resultCount>0){
            return ServiceResponse.createBySuccess("删除地址成功");
        }
        return ServiceResponse.createByErrorMessage("删除地址失败");
    }

    public ServiceResponse update(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount=shippingMapper.updateByShipping(shipping);
        if(rowCount>0){

            return ServiceResponse.createBySuccess("更新地址成功");
        }
        return ServiceResponse.createByErrorMessage("更新地址失败");

    }

    public ServiceResponse<Shipping> select(Integer userId, Integer shippingId){
        Shipping shipping = shippingMapper.selectByShippingIdUserId(userId, shippingId);
        if (shipping==null){
            return ServiceResponse.createByErrorMessage("无法查询到该地址");
        }
        return ServiceResponse.createBySuccess("更新地址成功",shipping);

    }

    public ServiceResponse<PageInfo> list(Integer userId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList=shippingMapper.selectByUserId(userId);
        PageInfo pageInfo=new PageInfo(shippingList);
        return ServiceResponse.createBySuccess(pageInfo);
    }



}
