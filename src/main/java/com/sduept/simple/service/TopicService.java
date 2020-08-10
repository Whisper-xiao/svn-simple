package com.sduept.simple.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sduept.simple.common.ServerResponse;
import com.sduept.simple.dto.MasterPostPage;
import com.sduept.simple.dto.SubPostPage;
import com.sduept.simple.entity.Topic;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Xiao guang zhen
 * @since 2020-03-22
 */
public interface TopicService extends IService<Topic> {

    /**
     * 分页查询主贴列表
     *
     * @param pageCondition {@link MasterPostPage}
     */
    ServerResponse listMasterPostByPage(MasterPostPage pageCondition);

    /**
     * 新增主贴
     *
     * @param topic {@link Topic}
     */
    ServerResponse addMasterPost(Topic topic);

    /**
     * 编辑主贴
     *
     * @param topic {@link Topic}
     */
    ServerResponse editMasterPost(Topic topic);

    /**
     * 删除主贴
     *
     * @param id 主贴ID
     */
    ServerResponse deleteMasterPost(Integer id);
}
