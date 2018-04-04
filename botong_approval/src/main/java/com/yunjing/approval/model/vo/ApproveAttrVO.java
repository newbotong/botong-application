package com.yunjing.approval.model.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.yunjing.approval.model.entity.ApproveAttr;
import com.yunjing.approval.util.ApproConstants;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author 刘小鹏
 * @date 2018/04/04
 */
@Data
public class ApproveAttrVO {

    public ApproveAttrVO() {

    }

    public ApproveAttrVO(ApproveAttributeVO attr) {
        String name = attr.getAttrLabel();

        String unit = attr.getAttrUnit();

        int type = attr.getAttrType();

        // 类型是时间区间（开始时间和结束时间）的情况
        if (type == ApproConstants.TIME_INTERVAL_TYPE_5) {
            this.label = attr.getAttrLabel();
            this.labels = attr.getAttrLabels();
        } else {
            if (StringUtils.isNotBlank(name)) {
                if (StringUtils.isNotBlank(unit)) {
                    this.label = name + "(" + unit + ")";
                } else {
                    this.label = name;
                }
            } else {
                this.label = attr.getAttrName();
            }
        }

        String value = attr.getAttrValue();
        if (StringUtils.isNotBlank(value)) {
            // 类型是附件的情况
            if (type == ApproConstants.ENCLOSURE_TYPE_11) {
                JSONArray jsonArray = JSONArray.parseArray(value);
                if (jsonArray != null) {
                    int size = jsonArray.size();
                    if (size > 0) {
                        List<FileVO> files = new ArrayList<>(size);
                        Iterator<Object> it = jsonArray.iterator();
                        while (it.hasNext()) {
                            JSONObject obj = (JSONObject) it.next();
                            FileVO fileVo = new FileVO(obj.getString("name"), obj.getString("size"), obj.getString("url"));
                            files.add(fileVo);
                        }

                        if (CollectionUtils.isNotEmpty(files)) {
                            this.files = files;
                        }
                    }
                }
                // 类型是图片的情况
            } else if (type == ApproConstants.PICTURE_TYPE_10) {
                JSONArray jsonArray = JSONArray.parseArray(value);
                if (jsonArray != null) {
                    int size = jsonArray.size();
                    if (size > 0) {
                        List<ImageVO> images = new ArrayList<>(size);
                        Iterator<Object> it = jsonArray.iterator();
                        while (it.hasNext()) {
                            JSONObject obj = (JSONObject) it.next();
                            ImageVO imageVo = new ImageVO(obj.getString("url"));
                            images.add(imageVo);
                        }

                        if (CollectionUtils.isNotEmpty(images)) {
                            this.images = images;
                        }
                    }
                }
            } else if (type == ApproConstants.TIME_INTERVAL_TYPE_5) {
                String[] time = attr.getAttrValue().split(",");
                this.value = time[0];
                this.values = time[1];
            } else {
                this.value = attr.getAttrValue();
            }
        }

        this.type = type;
        this.num = attr.getAttrNum();
        this.dateFormat = attr.getDateFormat();
    }

    private String label;
    private String labels;
    private String dateFormat;
    private String value;
    private String values;
    private Integer type;
    private Integer num;
    private List<ApproveRowVO> contents;

    /**
     * 图片（type = 10） 专用
     */

    private List<ImageVO> images;

    /**
     * 附件（type = 11） 专用
     */
    private List<FileVO> files;

}