package org.wowtools.tabledif;

import java.sql.Connection;

/**
 * 表
 *
 * @author liuyu
 * @date 2020/3/19
 */
public class Table {
    /**
     * 数据库连接
     */
    private final Connection connection;
    /**
     * 表名/视图名
     */
    private final String tableName;
    /**
     * 主键字段
     */
    private final Field keyField;
    /**
     * 数据是否按主键顺序存储，若是，sql不需要order by，效率更高一点
     */
    private final boolean keyOrdered;
    /**
     * 需比较的字段
     */
    private final Field[] compareFields;

    public Table(Connection connection, String tableName, Field keyField, boolean keyOrdered, Field[] compareFields) {
        this.connection = connection;
        this.tableName = tableName;
        this.keyField = keyField;
        this.keyOrdered = keyOrdered;
        this.compareFields = compareFields;
    }



    /**
     * 构造查询sql
     * @return
     */
    public String buildSql() {
        StringBuffer sb = new StringBuffer("select ");
        sb.append(keyField.getFileName());
        for (Field compareField : compareFields) {
            sb.append(",").append(compareField.getFileName());
        }
        sb.append(" from ").append(tableName);
        if (!keyOrdered) {
            sb.append(" order by ").append(keyField.getFileName());
        }
        return sb.toString();
    }

    public Connection getConnection() {
        return connection;
    }

    public String getTableName() {
        return tableName;
    }

    public Field getKeyField() {
        return keyField;
    }

    public boolean isKeyOrdered() {
        return keyOrdered;
    }

    public Field[] getCompareFields() {
        return compareFields;
    }
}
