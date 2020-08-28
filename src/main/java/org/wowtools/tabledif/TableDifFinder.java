package org.wowtools.tabledif;

import org.wowtools.tabledif.filevalueequals.FileValueEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;

/**
 * 表差异查询器
 *
 * @author liuyu
 * @date 2020/3/19
 */
public class TableDifFinder {
    /**
     * 查找两张表的差异
     *
     * @param tableA
     * @param tableB
     * @param dif
     */
    public static void find(Table tableA, Table tableB, TableDif dif) {
        try (Connection connA = tableA.getConnection()) {
            try (PreparedStatement pstmA = connA.prepareStatement(tableA.buildSql())) {
                pstmA.setFetchSize(2000);//setFetchSize的用法在各种数据库中略有不同，注意修改
                try (ResultSet rsA = pstmA.executeQuery()) {
                    try (Connection connB = tableB.getConnection()) {
                        try (PreparedStatement pstmB = connB.prepareStatement(tableB.buildSql())) {
                            pstmB.setFetchSize(2000);//setFetchSize的用法在各种数据库中略有不同，注意修改
                            try (ResultSet rsB = pstmB.executeQuery()) {
                                while (findEqual(tableA, tableB, dif, rsA, rsB)) {
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * AB游标中的数据都是主键按从小到大排序的，移动ab游标找主键相同的值
     *
     * @return 是否需要继续
     */
    private static boolean findEqual(Table tableA, Table tableB, TableDif dif, ResultSet rsA, ResultSet rsB) throws SQLException {
        Comparator keyComparator = dif.getKeyComparator();
        Object keyA;//当前游标A指向主键值
        Object keyB;//当前游标B指向主键值
        int keyCp;//主键比较结果
        boolean aHasNext;//a还有next
        boolean bHasNext;//b还有next
        /** 0、游标AB都移到第一行 **/
        aHasNext = rsA.next();
        bHasNext = rsB.next();
        if (!aHasNext && !bHasNext) {
            return false;
        }
        if (!aHasNext) {
            do {
                keyB = tableB.getKeyField().getFieldValueGetter().get(rsB, 1);
                dif.notInTableA(keyB, getValue(tableB, rsB));
            } while (rsB.next());
            return false;
        } else if (!bHasNext) {
            do {
                keyA = tableA.getKeyField().getFieldValueGetter().get(rsA, 1);
                dif.notInTableB(keyA, getValue(tableA, rsA));
            } while (rsA.next());
            return false;
        }
        /** 移动游标A或B，找到第一个相同的主键 **/
        keyA = tableA.getKeyField().getFieldValueGetter().get(rsA, 1);
        keyB = tableB.getKeyField().getFieldValueGetter().get(rsB, 1);
        do {
            keyCp = keyComparator.compare(keyA, keyB);
            if (keyCp < 0) {//a<b 移动a
                dif.notInTableB(keyA, getValue(tableA, rsA));
                if (rsA.next()) {
                    keyA = tableA.getKeyField().getFieldValueGetter().get(rsA, 1);
                    continue;
                } else {
                    do {
                        keyB = tableB.getKeyField().getFieldValueGetter().get(rsB, 1);
                        dif.notInTableA(keyB, getValue(tableB, rsB));
                    } while (rsB.next());
                    return false;
                }
            } else if (keyCp > 0) {//a>b
                dif.notInTableA(keyB, getValue(tableB, rsB));
                if (rsB.next()) {
                    keyB = tableB.getKeyField().getFieldValueGetter().get(rsB, 1);
                    continue;
                } else {
                    do {
                        keyA = tableA.getKeyField().getFieldValueGetter().get(rsA, 1);
                        dif.notInTableB(keyA, getValue(tableA, rsA));
                    } while (rsA.next());
                    return false;
                }
            } else {//a==b
                //比较字段是否一致
                valueEq(tableA, tableB, dif, rsA, rsB, keyA, keyB);
                return true;
            }
        } while (true);
    }

    /**
     * 比较两个字段值是否一致，并通知
     */
    private static boolean valueEq(Table tableA, Table tableB, TableDif dif, ResultSet rsA, ResultSet rsB, Object keyA, Object keyB) throws SQLException {
        Object[] vas = getValue(tableA, rsA);
        Object[] vbs = getValue(tableB, rsB);
        FileValueEquals[] fieldsComparator = dif.getFieldsComparator();

        for (int i = 0; i < vas.length; i++) {
            Object va = vas[i];
            Object vb = vbs[i];
            if (null == va) {
                if (null != vb) {
                    dif.difAb(keyA, vas, vbs, i);
                    return false;
                }
            } else {
                if (!fieldsComparator[i].equalsAb(va, vb)) {
                    dif.difAb(keyA, vas, vbs, i);
                    return false;
                }
            }
        }
        dif.equal(keyA, vas);
        return true;
    }

    /**
     * 获取比较字段的值
     *
     * @param table
     * @param rs
     * @return
     */
    private static Object[] getValue(Table table, ResultSet rs) throws SQLException {
        Field[] fields = table.getCompareFields();
        Object[] objs = new Object[fields.length];
        for (int i = 0; i < fields.length; i++) {
            objs[i] = fields[i].getFieldValueGetter().get(rs, i + 2);//rs.getObiect从1开始，第一位是主键，所以+2
        }
        return objs;
    }

}
