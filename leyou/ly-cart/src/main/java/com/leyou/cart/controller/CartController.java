package com.leyou.cart.controller;

import com.leyou.cart.entity.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class CartController {

    @Resource
    private CartService cartService;

    /**
     * 添加商品到购物车中
     * @param cart  购物车对象接收参数
     * @return      没有返回值
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 获取购物车列表
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<Cart>> findCartList(){
        List<Cart> cartList = cartService.findCartList();
        return ResponseEntity.ok(cartList);
    }

    /**
     * 批量添加购物车信息
     * @param cartList
     * @return
     */
    @PostMapping("/list")
    public ResponseEntity<Void> addCartList(@RequestBody List<Cart> cartList){
        cartService.addCartList(cartList);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
