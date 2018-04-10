package com.yunjing.info.processor.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.yunjing.info.common.InfoConstant;
import com.yunjing.info.model.InfoCatalog;
import com.yunjing.info.processor.mq.configuration.TimingMessageConfiguration;
import com.yunjing.info.service.impl.InfoCatalogServiceImpl;
import com.yunjing.message.annotation.MessageQueueDeclarable;
import com.yunjing.message.declare.consumer.AbstractMessageConsumerWithQueueDeclare;
import com.yunjing.message.model.Message;
import com.yunjing.message.share.org.OrgAppMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * <p> 提醒消息接受者
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/27.
 */
@Slf4j
@Component
@MessageQueueDeclarable
public class TimingMessageConsumer extends AbstractMessageConsumerWithQueueDeclare<Message, TimingMessageConfiguration> {

    @Autowired
    private InfoCatalogServiceImpl infoCatalogService;

    public TimingMessageConsumer(TimingMessageConfiguration configuration) {
        super(configuration);
    }

    @Override
    public void onMessageReceive(@Payload Message message) {
        log.info("【企业初始化监听消息结果】orgId:{}", JSON.toJSONString(message));
        //1、先需要判断类型是否为初始化
        OrgAppMessage orgAppMessage = (OrgAppMessage) message.getObj();
        if (orgAppMessage.getMessageType().equals(OrgAppMessage.MessageType.INIT)) {
            String orgId = orgAppMessage.getCompanyId();
            List<InfoCatalog> infoCatalogList = new InfoCatalog().selectList(new EntityWrapper<InfoCatalog>()
                    .eq("is_delete", InfoConstant.LOGIC_DELETE_NORMAL).eq("org_id", orgId));
            if (CollectionUtils.isEmpty(infoCatalogList)) {
                infoCatalogService.initCompany(orgId);
            }
        }
    }
}
