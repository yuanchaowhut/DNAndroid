package cn.com.egova.binderframework.inter;

import java.util.List;

import cn.com.egova.binderframework.bean.Student;
import cn.com.egova.binderframework.data.DataSource;

/**
 * Created by yuanchao on 2018/11/25.
 */

public class StudentManagerImp extends IStudentManager.Stub {

    @Override
    public Student getStudentById(String id) {
        Student result = null;
        for (Student student : DataSource.students) {
            if (student.getStuId().equals(id)) {
                result = student;
                break;
            }
        }
        return result;
    }

    @Override
    public List<Student> getAllStudents() {
        return DataSource.students;
    }
}
