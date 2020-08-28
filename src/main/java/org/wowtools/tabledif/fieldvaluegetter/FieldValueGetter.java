package org.wowtools.tabledif.fieldvaluegetter;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 字段值获取器
 *
 * @author liuyu
 * @date 2020/3/19
 */
@FunctionalInterface
public interface FieldValueGetter<T> {
    T get(ResultSet rs, int idx) throws SQLException;
}
