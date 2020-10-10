package com.wayn.common.core.service.shop.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wayn.common.core.domain.shop.Column;
import com.wayn.common.core.mapper.shop.ColumnMapper;
import com.wayn.common.core.service.shop.IColumnService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 首页栏目配置 服务实现类
 * </p>
 *
 * @author wayn
 * @since 2020-10-10
 */
@Service
public class ColumnServiceImpl extends ServiceImpl<ColumnMapper, Column> implements IColumnService {

}
