package org.wowtools.tabledif;

import org.wowtools.tabledif.filevalueequals.FileValueEquals;

import java.util.Comparator;

/**
 * 表差异监听器，当比较器发现差异时，推送给对应方法
 *
 * @author liuyu
 * @date 2020/3/19
 */
public interface TableDif<T> {

    /**
     * 获取主键的比较器
     *
     * @return
     */
    Comparator getKeyComparator();

    /**
     * 获取需比较的字段的比较器，顺序与传入的字段顺序相同
     *
     * @return
     */
    FileValueEquals[] getFieldsComparator();

    /**
     * B中有但A中没有
     *
     * @param key  差异的键值
     * @param rowB B中的值，顺序与传入的字段顺序相同
     */
    void notInTableA(T key, Object[] rowB);

    /**
     * A中有但B中没有
     *
     * @param key  差异的键值
     * @param rowA A中的值，顺序与传入的字段顺序相同
     */
    void notInTableB(T key, Object[] rowA);

    /**
     * AB都有，但值不一样
     *
     * @param key    差异的键值
     * @param rowA   A中的值，顺序与传入的字段顺序相同
     * @param rowB   B中的值，顺序与传入的字段顺序相同
     * @param difIdx 第一个有差异的字段index，不代表后续没差异
     */
    void difAb(T key, Object[] rowA, Object[] rowB, int difIdx);

    /**
     * 相同的值
     *
     * @param key
     * @param rowA
     */
    void equal(T key, Object[] rowA);
}
