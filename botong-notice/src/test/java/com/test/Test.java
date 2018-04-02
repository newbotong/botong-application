//package com.test;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.yunjing.notice.body.*;
//import com.yunjing.notice.body.base.BaseNoticeBody;
//import com.yunjing.notice.enums.NoticeTypeEnum;
//import com.yunjing.mommon.utils.IDUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
///**
// * <p>
// * <p>
// * </p>
// *
// * @author tao.zeng.
// * @since 2018/3/20.
// */
//public class Test {
//
//
//    private String getId() {
//        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
//    }
//
//    private Long getLongId() {
//        return IDUtils.getID();
//    }
//
//    @org.junit.Test
//    public void test1() {
//        for (int i = 0; i < 10; i++) {
//            System.out.println(getLongId());
//        }
//    }
//
//    /**
//     * 0-文本消息
//     * 1-语音消息（不可转发）
//     * 2-图片消息
//     * 3-视频消息
//     * 4-公告
//     * 5-评论
//     * 6-名片
//     * 7-地理位置信息
//     * 8-文件
//     */
//    @org.junit.Test
//    public void test() {
//        FavouriteBody rootBody = new FavouriteBody();
//        rootBody.setCreateTime(System.currentTimeMillis());
//        rootBody.setId(getLongId());
//        rootBody.setOrigin("王开亮");
//        rootBody.setUserId(getId());
//        List<BaseNoticeBody> favouriteBodies = new ArrayList<>();
//        BaseNoticeBody body = new BaseNoticeBody();
//        body.setTextBody(createTextBody());
//        body.setMultimediaBody(createMultimediaBody());
//        body.setNoticeDetailBody(createAddressBody());
//        body.setCardBody(createCardBody());
//        body.setCommentBody(createCommentBody());
//        body.setFileBody(createFileBody());
//        body.setNoticeBody(createNoticeBody());
//        body.setInformationBody(createInformationBody());
//        favouriteBodies.add(body);
//        rootBody.setBody(favouriteBodies);
//        Gson gson = new GsonBuilder().serializeNulls().create();
//        String json = gson.toJson(rootBody);
//        System.out.println(json);
//
//        FavouriteBody body1 = gson.fromJson(json, FavouriteBody.class);
//        System.out.println(body1);
//
//    }
//
//
//    /**
//     * 文本消息
//     *
//     * @return
//     */
//    private List<TextBody> createTextBody() {
//
//        List<TextBody> list = new ArrayList<>();
//
//        // 文本收藏
//        TextBody textBody;
//        for (int i = 0; i < 3; i++) {
//            textBody = new TextBody();
//            textBody.setOriginId(getId());
//            textBody.setIsDang(i % 2 == 0 ? 1 : 0);
//            textBody.setType(NoticeTypeEnum.TYPE_TEXT.getType());
//            textBody.setContent("收藏文本内容" + (i + 1));
//            list.add(textBody);
//        }
//        return list;
//    }
//
//    /**
//     * 多媒体
//     * <p>
//     * 1-语音消息（不可转发）
//     * 2-图片消息
//     * 3-视频消息
//     *
//     * @return
//     */
//    private List<MultimediaBody> createMultimediaBody() {
//
//        List<MultimediaBody> list = new ArrayList<>();
//
//        // 多媒体消息
//        MultimediaBody multimediaBody1 = new MultimediaBody();
//        multimediaBody1.setOriginId(getId());
//        multimediaBody1.setIsDang(0);
//        multimediaBody1.setType(NoticeTypeEnum.TYPE_PICTURE.getType());
//        multimediaBody1.setUrl("http://pic.58pic.com/58pic/13/31/44/59G58PICVu4_1024.jpg");
//
//        MultimediaBody multimediaBody2 = new MultimediaBody();
//        multimediaBody2.setOriginId(getId());
//        multimediaBody2.setIsDang(0);
//        multimediaBody2.setType(NoticeTypeEnum.TYPE_VIDEO.getType());
//        multimediaBody2.setUrl("http://pic.58pic.com/58pic/13/31/44/59G58PICVu4_1024.mp4");
//
//        MultimediaBody multimediaBody3 = new MultimediaBody();
//        multimediaBody3.setOriginId(getId());
//        multimediaBody3.setIsDang(0);
//        multimediaBody3.setType(NoticeTypeEnum.TYPE_VOICE.getType());
//        multimediaBody3.setUrl("http://pic.58pic.com/58pic/13/31/44/59G58PICVu4_1024.mp3");
//
//        list.add(multimediaBody1);
//        list.add(multimediaBody2);
//        list.add(multimediaBody3);
//
//
//        return list;
//    }
//
//    /**
//     * 公告
//     *
//     * @return
//     */
//    private List<NoticeBody> createNoticeBody() {
//        List<NoticeBody> list = new ArrayList<>();
//        // 公告
//        NoticeBody noticeBody = new NoticeBody();
//        noticeBody.setOriginId(getId());
//        noticeBody.setIsDang(0);
//        noticeBody.setType(NoticeTypeEnum.TYPE_NOTICE.getType());
//        noticeBody.setAuthor("公告作者");
//        noticeBody.setName("公告名称标题");
//        noticeBody.setContent("公告内容");
//        list.add(noticeBody);
//        return list;
//    }
//
//    /**
//     * 评论
//     *
//     * @return
//     */
//    private List<CommentBody> createCommentBody() {
//        List<CommentBody> list = new ArrayList<>();
//        // 评论
//        CommentBody commentBody = new CommentBody();
//        commentBody.setOriginId(getId());
//        commentBody.setIsDang(0);
//        commentBody.setType(NoticeTypeEnum.TYPE_COMMENT.getType());
//        commentBody.setContent("评论内容");
//        list.add(commentBody);
//        return list;
//    }
//
//    /**
//     * 名片
//     *
//     * @return
//     */
//    private List<CardBody> createCardBody() {
//        List<CardBody> list = new ArrayList<>();
//        // 名片
//        CardBody cardBody = new CardBody();
//        cardBody.setOriginId(getId());
//        cardBody.setIsDang(0);
//        cardBody.setType(NoticeTypeEnum.TYPE_CARD.getType());
//        cardBody.setJob("工作职位");
//        cardBody.setName("风清扬-这是名称");
//        cardBody.setPicture("http://pic.58pic.com/58pic/13/31/44/59G58PICVu4_1024.jpg");
//        cardBody.setUserId(getId());
//
//        list.add(cardBody);
//        return list;
//    }
//
//    /**
//     * 地理位置信息
//     *
//     * @return
//     */
//    private List<NoticeDetailBody> createAddressBody() {
//        List<NoticeDetailBody> list = new ArrayList<>();
//        // 地理位置
//        NoticeDetailBody noticeDetailBody = new NoticeDetailBody();
//        noticeDetailBody.setOriginId(getId());
//        noticeDetailBody.setIsDang(0);
//        noticeDetailBody.setType(NoticeTypeEnum.TYPE_CARD.getType());
//        noticeDetailBody.setUrl("地址 url");
//        noticeDetailBody.setLatitude("纬度");
//        noticeDetailBody.setLongitude("经度");
//
//        list.add(noticeDetailBody);
//        return list;
//    }
//
//    /**
//     * 文件
//     *
//     * @return
//     */
//    private List<FileBody> createFileBody() {
//        List<FileBody> list = new ArrayList<>();
//        // 文件
//        FileBody fileBody = new FileBody();
//        fileBody.setOriginId(getId());
//        fileBody.setIsDang(0);
//        fileBody.setType(NoticeTypeEnum.TYPE_ADDRESS.getType());
//        fileBody.setName("文件名称");
//        fileBody.setPath("文件路径");
//        fileBody.setSize(1024L);
//        list.add(fileBody);
//        return list;
//    }
//
//    /**
//     * 文件
//     *
//     * @return
//     */
//    private List<InformationBody> createInformationBody() {
//        List<InformationBody> list = new ArrayList<>();
//        // 文件
//        InformationBody informationBody = new InformationBody();
//        informationBody.setOriginId(getId());
//        informationBody.setIsDang(0);
//        informationBody.setType(NoticeTypeEnum.TYPE_INFORMATION.getType());
//        informationBody.setName("文件名称");
//        informationBody.setName("资讯名称");
//        informationBody.setContent("资讯内容");
//        informationBody.setPicture("http://pic.58pic.com/58pic/13/31/44/59G58PICVu4_1024.jpg");
//        list.add(informationBody);
//        return list;
//    }
//}
