package org.wowtools.tabledif;

import org.wowtools.tabledif.fieldvaluegetter.FieldValueGetter;

import java.util.Comparator;

/**
 * 字段
 *
 * @author liuyu
 * @date 2020/3/19
 */
public class Field<T> {
    private final String fileName;
    private final FieldValueGetter<T> fieldValueGetter;

    public Field(String fileName, FieldValueGetter<T> fieldValueGetter) {
        this.fileName = fileName;
        this.fieldValueGetter = fieldValueGetter;
    }

    public String getFileName() {
        return fileName;
    }

    public FieldValueGetter<T> getFieldValueGetter() {
        return fieldValueGetter;
    }
}
