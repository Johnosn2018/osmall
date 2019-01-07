package com.osmall.user.vo;

/**
 * Created by Johnson on 2018/8/11.
 */
public class UserStatisticVo {
    private Long userCount;
    private Long productCount;
    private Long orderCount;

    public UserStatisticVo(Long userCount, Long productCount, Long orderCount) {
        this.userCount = userCount;
        this.productCount = productCount;
        this.orderCount = orderCount;
    }

    public Long getUserCount() {
        return userCount;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    public Long getProductCount() {
        return productCount;
    }

    public void setProductCount(Long productCount) {
        this.productCount = productCount;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Long orderCount) {
        this.orderCount = orderCount;
    }
}
