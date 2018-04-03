package com.yunjing.info.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.info.mapper.InfoDictionaryMapper;
import com.yunjing.info.model.InfoDictionary;
import com.yunjing.info.service.InfoDictionaryService;
import org.springframework.stereotype.Service;

/**
 * 资讯字典实现类
 *
 * @author 李双喜
 * @date 2018/4/3 12:51
 */
@Service
public class InfoDictionaryServiceImpl extends ServiceImpl<InfoDictionaryMapper, InfoDictionary> implements InfoDictionaryService {
}
