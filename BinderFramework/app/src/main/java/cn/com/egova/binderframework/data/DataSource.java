package cn.com.egova.binderframework.data;

import java.util.ArrayList;
import java.util.List;

import cn.com.egova.binderframework.bean.Student;

/**
 * Created by yuanchao on 2018/11/25.
 * 模拟数据库
 */

public class DataSource {

    public static List<Student> students = new ArrayList<>();

    static {
        students.add(new Student("001", "宋远桥", 55));
        students.add(new Student("002", "俞莲舟", 50));
        students.add(new Student("003", "俞岱岩", 48));
        students.add(new Student("004", "张松溪", 46));
        students.add(new Student("005", "张翠山", 44));
        students.add(new Student("006", "殷梨亭", 40));
        students.add(new Student("007", "莫声谷", 39));
    }
}
