package org.wowtools.tabledif.filevalueequals;

/**
 * 值比较器
 *
 * @author liuyu
 * @date 2020/3/19
 */
public interface FileValueEquals<T> {
    boolean equalsAb(T a, T b);
}
