// IStudentManager.aidl
package cn.com.egova.binderframework.inter;

// Declare any non-default types here with import statements
import cn.com.egova.binderframework.bean.Student;

interface IStudentManager {

   Student getStudentById(String id);

   List<Student> getAllStudents();
}
