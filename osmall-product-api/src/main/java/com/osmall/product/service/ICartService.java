package com.osmall.product.service;

import com.osmall.common.ServiceResponse;
import com.osmall.product.vo.CartVo;

/**
 * Created by Johnson on 2018/4/15.
 */
public interface ICartService {
    ServiceResponse<CartVo> add(Integer userId, Integer productId, Integer count);
    ServiceResponse<CartVo> update(Integer userId, Integer productId, Integer count);
    ServiceResponse<CartVo> deleteProduct(Integer userId, String productIds);
    ServiceResponse<CartVo> list(Integer userId);
    ServiceResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked);
    ServiceResponse<Integer> getCartProductCount(Integer userId);

}
