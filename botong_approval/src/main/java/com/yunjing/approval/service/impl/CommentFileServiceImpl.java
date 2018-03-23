package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.mapper.CommentFileMapper;
import com.yunjing.approval.model.entity.CommentFile;
import com.yunjing.approval.service.ICommentFileService;
import org.springframework.stereotype.Service;

/**
 * @author liuxiaopeng
 * @date 2018/1/15
 */
@Service
public class CommentFileServiceImpl extends BaseServiceImpl<CommentFileMapper, CommentFile> implements ICommentFileService {
}
