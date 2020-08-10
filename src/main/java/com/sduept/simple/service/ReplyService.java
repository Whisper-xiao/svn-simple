package com.sduept.simple.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sduept.simple.common.ServerResponse;
import com.sduept.simple.dto.SubPostPage;
import com.sduept.simple.entity.Reply;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Xiao guang zhen
 * @since 2020-03-22
 */
public interface ReplyService extends IService<Reply> {

    /**
     * 根据主贴ID分页查询所有的子贴列表
     *
     * @param pageCondition {@link SubPostPage}
     */
    ServerResponse listSubPost(SubPostPage pageCondition);

    /**
     * 新增子贴
     *
     * @param reply {@link Reply}
     */
    ServerResponse addSubPost(Reply reply);
}
