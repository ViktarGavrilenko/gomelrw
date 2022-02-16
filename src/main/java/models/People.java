package models;

import java.util.Date;

public class People {
    public int id;
    public String i;
    public String f;
    public String o;
    public Date dt_birthday;
    public String sex;

    public People(String f, String i, String o, Date dt_birthday, String sex) {
        this.f = f;
        this.i = i;
        this.o = o;
        this.dt_birthday = dt_birthday;
        this.sex = sex;
    }
}