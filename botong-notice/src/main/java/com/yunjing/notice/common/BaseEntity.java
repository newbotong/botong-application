package com.yunjing.notice.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.yunjing.mommon.utils.IDUtils;

import java.io.Serializable;

/**
 * 实体基类
 * @author 李双喜
 * @date 2018/3/21 14:10
 */
public class BaseEntity<T extends BaseEntity> extends Model<T> implements Cloneable {
    private Long id;
    @TableField("logic_delete")
    @JSONField(
            name = "logic_delete"
    )
    private Integer logicDelete;
    @TableField("create_time")
    @JSONField(
            name = "create_time"
    )
    private Long createTime;
    @TableField("update_time")
    @JSONField(
            name = "update_time"
    )
    private Long updateTime;

    public void preInsert() {
        this.setId(IDUtils.getID());
        this.logicDelete = 0;
        this.createTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
    }

    public void preUpdate() {
        this.updateTime = System.currentTimeMillis();
    }

    public void preDelete() {
        this.logicDelete = 1;
        this.updateTime = System.currentTimeMillis();
    }
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
    @Override
    public Object clone() {
        BaseEntity t = null;

        try {
            t = (BaseEntity)super.clone();
        } catch (CloneNotSupportedException var3) {
            var3.printStackTrace();
        }

        return t;
    }

    public BaseEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public Integer getLogicDelete() {
        return this.logicDelete;
    }

    public Long getCreateTime() {
        return this.createTime;
    }

    public Long getUpdateTime() {
        return this.updateTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLogicDelete(Integer logicDelete) {
        this.logicDelete = logicDelete;
    }

    public void setcreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public void setupdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof BaseEntity)) {
            return false;
        } else {
            BaseEntity<?> other = (BaseEntity)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label59: {
                    Object this$id = this.getId();
                    Object other$id = other.getId();
                    if (this$id == null) {
                        if (other$id == null) {
                            break label59;
                        }
                    } else if (this$id.equals(other$id)) {
                        break label59;
                    }

                    return false;
                }

                Object this$logicDelete = this.getLogicDelete();
                Object other$logicDelete = other.getLogicDelete();
                if (this$logicDelete == null) {
                    if (other$logicDelete != null) {
                        return false;
                    }
                } else if (!this$logicDelete.equals(other$logicDelete)) {
                    return false;
                }

                Object this$createTime = this.getCreateTime();
                Object other$createTime = other.getCreateTime();
                if (this$createTime == null) {
                    if (other$createTime != null) {
                        return false;
                    }
                } else if (!this$createTime.equals(other$createTime)) {
                    return false;
                }

                Object this$updateTime = this.getUpdateTime();
                Object other$updateTime = other.getUpdateTime();
                if (this$updateTime == null) {
                    if (other$updateTime != null) {
                        return false;
                    }
                } else if (!this$updateTime.equals(other$updateTime)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof BaseEntity;
    }
//    @Override
//    public int hashCode() {
//        int PRIME = true;
//        int result = 1;
//        Object $id = this.getId();
//        int result = result * 59 + ($id == null ? 43 : $id.hashCode());
//        Object $logicDelete = this.getLogicDelete();
//        result = result * 59 + ($logicDelete == null ? 43 : $logicDelete.hashCode());
//        Object $createTime = this.getCreateTime();
//        result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
//        Object $updateTime = this.getUpdateTime();
//        result = result * 59 + ($updateTime == null ? 43 : $updateTime.hashCode());
//        return result;
//    }
    @Override
    public String toString() {
        return "BaseEntity(id=" + this.getId() + ", logicDelete=" + this.getLogicDelete() + ", createTime=" + this.getCreateTime() + ", updateTime=" + this.getUpdateTime() + ")";
    }
}
