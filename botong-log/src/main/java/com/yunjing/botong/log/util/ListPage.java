package com.yunjing.botong.log.util;

import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * <p> list 分页
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/4/9.
 */
@Getter
public class ListPage<T> {

    /**
     * 每页显示条数
     */
    private int pageSize;

    /**
     * 总页数
     */
    private int pageCount;

    /**
     * 原集合
     */
    private List<T> data;

    public ListPage(List<T> data, int pageSize) {
        if (CollectionUtils.isEmpty(data)) {
            throw new IllegalArgumentException("data must be not empty!");
        }
        this.data = data;
        this.pageSize = pageSize;
        this.pageCount = data.size() / pageSize;
        if (data.size() % pageSize != 0) {
            this.pageCount++;
        }
    }

    /**
     * 得到分页后的数据
     *
     * @param pageNo 页码
     * @return 分页后结果
     */
    public List<T> getPagedList(int pageNo) {
        int fromIndex = (pageNo - 1) * pageSize;
        if (fromIndex >= data.size()) {
            return Collections.emptyList();
        }

        int toIndex = pageNo * pageSize;
        if (toIndex >= data.size()) {
            toIndex = data.size();
        }
        return data.subList(fromIndex, toIndex);
    }
}
