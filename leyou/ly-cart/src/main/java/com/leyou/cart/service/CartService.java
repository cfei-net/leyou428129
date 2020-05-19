package com.leyou.cart.service;

import com.leyou.LyCartApplication;
import com.leyou.cart.entity.Cart;
import com.leyou.common.auth.pojo.UserInfo;
import com.leyou.common.auth.utils.UserHolder;
import com.leyou.common.constants.LyConstants;
import com.leyou.common.exception.LyException;
import com.leyou.common.exception.enums.ExceptionEnum;
import com.leyou.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 添加商品到购物车中
     * @param cart  购物车对象接收参数
     */
    public void addCart(Cart cart) {
        // 1、获取当前用户信息
        UserInfo user = UserHolder.getUser();
        if(user == null){
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
        // 2、拼接第一层Key
        String key = LyConstants.CART_PRE + user.getId();
        // 3、从redis中取出值
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(key);
        // 4、判断是否存在这个sku
        // 4.1 skuId : 第二层key
        String skuId = cart.getSkuId().toString();
        // 4.2 如果已经存在更新数量即可
        if(hashOps.hasKey(skuId)){
            // 改数量
            String cacheCart = hashOps.get(skuId);
            // 转成对象
            Cart oldCart = JsonUtils.toBean(cacheCart, Cart.class);
            // 修改数量
            cart.setNum(oldCart.getNum() + cart.getNum());
        }
        // 5、更新到redis
        hashOps.put(skuId, JsonUtils.toString(cart));
    }

    /**
     * 获取购物车列表
     * @return
     */
    public List<Cart> findCartList() {
        // 1、获取当前用户信息
        UserInfo user = UserHolder.getUser();
        if(user == null){
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
        // 2、拼接第一层Key
        String key = LyConstants.CART_PRE + user.getId();
        // 3、判断购物车是否存在
        if(!redisTemplate.hasKey(key)){
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        // 4、获取对象返回
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(key);
        // 5、获取值:  取出所有的value
        List<String> carts = hashOps.values();
        if(CollectionUtils.isEmpty(carts)){
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        // 6、把字符串转成对象
        List<Cart> cartList = carts.stream().map(c -> JsonUtils.toBean(c, Cart.class)).collect(Collectors.toList());
        // 7、返回
        return cartList;
    }

    /**
     * 批量添加购物车
     * @param cartList
     */
    public void addCartList(List<Cart> cartList) {
        // 批量添加购物车信息： 问题？ 在for循环中操作redis
        /*for (Cart cart : cartList) {
            addCart(cart);
        }*/

        // 1、获取当前用户信息
        UserInfo user = UserHolder.getUser();
        if(user == null){
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
        // 2、拼接第一层Key
        String key = LyConstants.CART_PRE + user.getId();

        // 在redis中有一个管道： Pipeline： 可以封装多次请求一次性到达 redis中
        redisTemplate.executePipelined(new RedisCallback<List<Cart>>() {
            @Override
            public List<Cart> doInRedis(RedisConnection connection) throws DataAccessException {
                // 打开管道
                connection.openPipeline();
                // 迭代参数，去做批处理
                for (Cart cart : cartList) {
                    try {
                        // 获取字符串
                        byte[] bytes = connection.hGet(key.getBytes(), cart.getSkuId().toString().getBytes());
                        if(bytes != null){
                            String json = new String(bytes, "UTF-8");
                            Cart cacheCart = JsonUtils.toBean(json, Cart.class);
                            // 修改数量
                            cart.setNum(cart.getNum() + cacheCart.getNum());
                        }
                        String cartStr = JsonUtils.toString(cart);
                        // 把购物车设置进去
                        connection.hSet(key.getBytes(), cart.getSkuId().toString().getBytes(), cartStr.getBytes());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        });
    }
}
