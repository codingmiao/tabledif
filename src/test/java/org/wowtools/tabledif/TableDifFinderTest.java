package org.wowtools.tabledif;

import org.junit.Test;
import org.wowtools.tabledif.filevalueequals.FileValueEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Comparator;

public class TableDifFinderTest {

    @Test
    public void find() {
        //定义表A
        Table tableA = new Table(getConn(),
                "table1",
                new Field("id", (rs, idx) -> {
                    return rs.getObject(idx);
                }),
                false,
                new Field[]{
                        new Field("name", (rs, idx) -> {
                            return rs.getObject(idx);
                        }),
                }
        );
        //定义表B
        Table tableB = new Table(getConn(),
                "table2",
                new Field("id", (rs, idx) -> {
                    return rs.getObject(idx);
                }),
                false,
                new Field[]{
                        new Field("name", (rs, idx) -> {
                            return rs.getObject(idx);
                        }),
                }
        );
        //定义比较器
        TableDif dif = new TableDif() {
            @Override
            public Comparator getKeyComparator() {
                return Comparator.comparingInt(o -> (int) o);
            }

            @Override
            public FileValueEquals[] getFieldsComparator() {
                //比较每个字段的比较器数组
                return new FileValueEquals[]{
                        (a, b) -> {
                            if (a == null) {
                                return b == null;
                            } else {
                                return a.equals(b);
                            }
                        }
                };
            }

            //各类状态的操作实现,这里把状态和id打印出来，你也可以把它写入数据库之类
            @Override
            public void notInTableA(Object key, Object[] rowB) {
                System.out.println("notInTableA " + key);
            }

            @Override
            public void notInTableB(Object key, Object[] rowA) {
                System.out.println("notInTableB " + key);
            }

            @Override
            public void difAb(Object key, Object[] rowA, Object[] rowB, int difIdx) {
                System.out.println("difAb " + key);
            }

            @Override
            public void equal(Object key, Object[] rowA) {
                System.out.println("equal " + key);
            }
        };
        TableDifFinder.find(tableA, tableB, dif);
    }


    private Connection getConn() {
        String url = "jdbc:postgresql://localhost:5432/test1";
        String username = "pg";
        String password = "123";
        Connection connection;
        try {
            Class.forName("org.postgresql.Driver").newInstance();
            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);//setFetchSize的用法在各种数据库中略有不同，注意修改。postgresql需要setAutoCommit(false)
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return connection;
    }
}
