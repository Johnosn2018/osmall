package com.osmall.product.service;

import com.github.pagehelper.PageInfo;
import com.osmall.common.ServiceResponse;
import com.osmall.product.pojo.Shipping;

/**
 * Created by Johnson on 2018/4/16.
 */
public interface IShippingService {
    ServiceResponse add(Integer userId, Shipping shipping);
    ServiceResponse<String> del(Integer userId, Integer shippingId);
    ServiceResponse update(Integer userId, Shipping shipping);
    ServiceResponse<Shipping> select(Integer userId, Integer shippingId);
    ServiceResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);
}
