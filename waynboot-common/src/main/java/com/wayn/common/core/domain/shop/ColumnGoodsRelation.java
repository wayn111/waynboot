package com.wayn.common.core.domain.shop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 栏目商品关联表
 * </p>
 *
 * @author wayn
 * @since 2020-10-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ColumnGoodsRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 栏目ID
     */
    private Integer columnId;

    /**
     * 商品ID
     */
    private Integer goodsId;


}
