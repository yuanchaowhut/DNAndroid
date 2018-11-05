package cn.com.egova.dbframework.bean;

import cn.com.egova.dbframework.annotation.DbTable;

/**
 * Created by yuanchao on 2018/3/14.
 */

@DbTable("t_photo")
public class Photo {
    private String path;
    private String time;

    public Photo() {
    }

    public Photo(String path, String time) {
        this.path = path;
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "path='" + path + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
