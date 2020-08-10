package com.wayn.mobile.api.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wayn.common.base.BaseController;
import com.wayn.common.util.R;
import com.wayn.mobile.api.domain.Cart;
import com.wayn.mobile.api.service.ICartService;
import com.wayn.mobile.framework.security.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 购物车商品表 前端控制器
 * </p>
 *
 * @author wayn
 * @since 2020-08-03
 */
@RestController
@RequestMapping("cart")
public class CartController extends BaseController {

    @Autowired
    private ICartService iCartService;

    @GetMapping("list")
    public R list() {
        Long userId = SecurityUtils.getLoginUser().getMember().getId();
        return iCartService.list(userId);
    }

    @PostMapping
    public R add(@RequestBody Cart cart) {
        return iCartService.addCart(cart);
    }

    @PutMapping
    public R update(@RequestBody Cart cart) {
        return R.result(iCartService.updateById(cart));
    }

    @PostMapping("addNum/{cartId}/{number}")
    public R addNum(@PathVariable Long cartId, @PathVariable Integer number) {
        return iCartService.addNum(cartId, number);
    }

    @PostMapping("minusNum/{cartId}/{number}")
    public R minusNum(@PathVariable Long cartId, @PathVariable Integer number) {
        return iCartService.minusNum(cartId, number);
    }

    @DeleteMapping("{cartId}")
    public R delete(@PathVariable Long cartId) {
        return R.result(iCartService.removeById(cartId));
    }

    @GetMapping("goodsCount")
    public R goodsCount() {
        return iCartService.goodsCount();
    }

    @PostMapping("getCheckedGoods")
    public R getCheckedGoods() {
        Long userId = SecurityUtils.getLoginUser().getMember().getId();
        List<Cart> cartList = iCartService.list(new QueryWrapper<Cart>()
                .eq("user_id", userId).eq("checked", true));
        BigDecimal amount = new BigDecimal(0.00);
        for (Cart cart : cartList) {
            amount = amount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
        }
        return R.success().add("data", cartList).add("amount", amount);
    }
}