package com.yunjing.log.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/4/10.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "date_test")
public class DateTest {

    private Date date;
    private String name;
}
