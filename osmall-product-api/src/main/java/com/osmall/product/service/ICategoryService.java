package com.osmall.product.service;

import com.osmall.common.ServiceResponse;
import com.osmall.product.pojo.Category;

import java.util.List;


public interface ICategoryService {//
    ServiceResponse addCategory(String categoryName, Integer parentId);
    ServiceResponse updateCategoryName(Integer categoryId, String categoryName);
    ServiceResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);
    ServiceResponse< List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
