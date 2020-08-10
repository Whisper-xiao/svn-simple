package com.sduept.simple.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sduept.simple.entity.Type;
import com.sduept.simple.mapper.TypeMapper;
import com.sduept.simple.service.TypeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Xiao guang zhen
 * @since 2020-03-21
 */
@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type> implements TypeService {

}
