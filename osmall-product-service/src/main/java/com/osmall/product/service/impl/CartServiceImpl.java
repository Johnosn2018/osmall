package com.osmall.product.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.osmall.common.Const;
import com.osmall.common.ResponseCode;
import com.osmall.common.ServiceResponse;
import com.osmall.product.mapper.CartMapper;
import com.osmall.product.mapper.ProductMapper;
import com.osmall.product.pojo.Cart;
import com.osmall.product.pojo.Product;
import com.osmall.product.service.ICartService;
import com.osmall.util.BigDecimalUtil;
import com.osmall.util.PropertiesUtil;
import com.osmall.product.vo.CartProductVo;
import com.osmall.product.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    public ServiceResponse<CartVo> add(Integer userId,Integer productId,Integer count){
        if(productId==null||count==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart=cartMapper.selectCartByUserIdProductId(userId,productId);
        if(cart==null){
            //这个产品不在这个购物车里，需要新增一个产品的记录
            Cart cartItem=new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        }else{
            //这个产品已经在购物车里了
            //如果产品已经存在，数量相加
            count=cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        CartVo cartVo=this.getCartVoLimit(userId);

        return ServiceResponse.createBySuccess(cartVo);
    }

    public ServiceResponse<CartVo> update(Integer  userId,Integer productId,Integer count){
        if(productId==null||count==null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
        if(cart!=null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        CartVo cartVo=this.getCartVoLimit(userId);
        return ServiceResponse.createBySuccess(cartVo);
    }

    public ServiceResponse<CartVo> deleteProduct(Integer userId,String productIds){
        List<String> productList= Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productList)){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId,productList);
        CartVo cartVo=this.getCartVoLimit(userId);
        return ServiceResponse.createBySuccess(cartVo);
    }


    public ServiceResponse<CartVo> list(Integer userId){
        CartVo cartVo=this.getCartVoLimit(userId);
        return ServiceResponse.createBySuccess(cartVo);
    }

    public ServiceResponse<CartVo> selectOrUnSelect(Integer userId,Integer productId,Integer checked){
        cartMapper.checkedOrUncheckedProduct(userId,productId,checked);
        return this.list(userId);
    }

    public ServiceResponse<Integer> getCartProductCount(Integer userId){
        if(userId == null){
            return ServiceResponse.createBySuccess(0);
        }
        return ServiceResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }




    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo=new CartVo();
        List<Cart> cartList=cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList= Lists.newArrayList();

        BigDecimal cartTotalPrice=new BigDecimal("0");
        if(CollectionUtils.isNotEmpty(cartList)){
            for(Cart cartItem : cartList){
                CartProductVo cartProductVo=new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());

                Product product=productMapper.selectByPrimaryKey(cartItem.getProductId());
                if(product!=null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount=0;
                    if(product.getStock()>=cartItem.getQuantity()){
                        //库存充足的时候
                        buyLimitCount=cartItem.getQuantity();
                        cartProductVo.setLimitQuantiry(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        buyLimitCount=product.getStock();
                        cartProductVo.setLimitQuantiry(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity=new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                    if(cartItem.getChecked()==Const.Cart.CHECKED){
                        //如果已经勾选，增加到整个的购物车总价中
                        //cartTotalPrice=BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                        cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());

                       // System.out.println(cartTotalPrice);
                    }
                }
                /*if(cartItem.getChecked()==Const.Cart.CHECKED){
                    //如果已经勾选，增加到整个的购物车总价中
                    //cartTotalPrice=BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                   cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());

                    System.out.println(cartTotalPrice);
                }*/
                cartProductVoList.add(cartProductVo);

            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    private boolean getAllCheckedStatus(Integer userId){
        if(userId==null){
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId)==0;
    }
}
