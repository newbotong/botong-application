package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.approval.dao.mapper.CommentImgMapper;
import com.yunjing.approval.model.entity.CommentImg;
import com.yunjing.approval.service.ICommentImgService;
import org.springframework.stereotype.Service;

/**
 * @author liuxiaopeng
 * @date 2018/1/15
 */
@Service
public class CommentImgServiceImpl extends ServiceImpl<CommentImgMapper, CommentImg> implements ICommentImgService {
}
