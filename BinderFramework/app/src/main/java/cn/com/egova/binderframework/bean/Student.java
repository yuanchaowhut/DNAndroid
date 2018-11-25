package cn.com.egova.binderframework.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yuanchao on 2018/11/25.
 */

public class Student implements Parcelable {
    private String stuId;
    private String name;
    private int age;

    public Student() {
    }

    public Student(String stuId, String name, int age) {
        this.stuId = stuId;
        this.name = name;
        this.age = age;
    }

    public Student(Parcel in) {
        this.stuId = in.readString();
        this.name = in.readString();
        this.age = in.readInt();
    }

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "stuId='" + stuId + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel source) {
            return new Student(source);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stuId);
        dest.writeString(name);
        dest.writeInt(age);
    }
}
