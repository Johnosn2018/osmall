package com.osmall.product.service;

import com.github.pagehelper.PageInfo;
import com.osmall.common.ServiceResponse;
import com.osmall.product.pojo.Product;
import com.osmall.product.vo.ProductDetailVo;

/**
 * Created by Johnson on 2018/4/13.
 */
public interface IProductService {
    ServiceResponse saveOrUpdateProduct(Product product);
    ServiceResponse<String> setSaleStatus(Integer productId, Integer status);
    ServiceResponse<ProductDetailVo> manageProductDetail(Integer productId);
    ServiceResponse<PageInfo> getProductList(int pageNum, int pageSize);
    ServiceResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize);

    ServiceResponse<ProductDetailVo> getProductDetail(Integer productId);

    ServiceResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy);
}
