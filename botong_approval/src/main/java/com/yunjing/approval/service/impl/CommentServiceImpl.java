package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.approval.dao.mapper.CommentMapper;
import com.yunjing.approval.model.entity.Comment;
import com.yunjing.approval.service.ICommentService;
import org.springframework.stereotype.Service;

/**
 * @author liuxiaopeng
 * @date 2018/1/15
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {
}
