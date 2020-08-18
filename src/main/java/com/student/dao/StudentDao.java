package com.student.dao;

import com.student.entity.Student;
import com.ysmart.annotation.Autowired;
import com.ysmart.annotation.Mapping;

@Mapping
public class StudentDao {
    @Autowired
    private Student student;

    public String getStudent(){
        return student.toString();
    }
}
