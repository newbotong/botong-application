package com.yunjing.approval.model.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.yunjing.approval.model.entity.ApproveAttr;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author liuxiaopeng
 */
@Data
public class ApproveAttrVO {

    public ApproveAttrVO() {

    }

    public ApproveAttrVO(ApproveAttr attr) {
        String name = attr.getAttrLabel();
        String unit = attr.getAttrUnit();

        int type = attr.getAttrType();

        if (type == 5) {
            String custom = attr.getOptValue();
            if (StringUtils.isNotBlank(custom)) {
                this.name = attr.getOptValue();
            } else {
                this.name = "开始时间,结束时间";
            }
        } else {
            if (StringUtils.isNotBlank(name)) {
                if (StringUtils.isNotBlank(unit)) {
                    this.name = name + "(" + unit + ")";
                } else {
                    this.name = name;
                }
            } else {
                this.name = attr.getAttrName();
            }
        }

        String value = attr.getAttrValue();
        if (StringUtils.isNotBlank(value)) {
            if (type == 11) {
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
            } else if (type == 12) {
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
            } else {
                this.value = attr.getAttrValue();
            }
        }

        this.type = type;
        this.num = attr.getAttrNum();
    }

    private String name;
    private String value;
    private Integer type;
    private Integer num;
    private List<ApproveRowVO> details;

    /**
     * type = 12 专用
     */

    private List<ImageVO> images = new ArrayList<>();

    /**
     * type = 11 专用
     */
    private List<FileVO> files = new ArrayList<>();

}