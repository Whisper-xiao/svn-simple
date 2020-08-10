package com.sduept.simple.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sduept.simple.common.ServerResponse;
import com.sduept.simple.dto.SubPostPage;
import com.sduept.simple.entity.Reply;
import com.sduept.simple.mapper.ReplyMapper;
import com.sduept.simple.service.ReplyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Xiao guang zhen
 * @since 2020-03-22
 */
@Service
public class ReplyServiceImpl extends ServiceImpl<ReplyMapper, Reply> implements ReplyService {

    @Override
    public ServerResponse listSubPost(SubPostPage pageCondition) {
        if (Objects.isNull(pageCondition.getTopicId())) {
            return ServerResponse.createByErrorMessage("主任务的标识不能为空");
        }

        Page<Reply> pageRe = new Page<>(pageCondition.getPageNum(), pageCondition.getPageSize());
        return ServerResponse.createBySuccess(page(pageRe, new QueryWrapper<Reply>().lambda().eq(Reply::gettId, pageCondition.getTopicId())));
    }

    @Override
    public ServerResponse addSubPost(Reply reply) {
        boolean isSuccessed = save(reply);
        if (isSuccessed) {
            return ServerResponse.createBySuccessMessage("回复成功");
        }
        return ServerResponse.createByErrorMessage("回复失败");
    }
}
