package com.student;

import com.student.dao.StudentDao;
import com.ysmart.annotation.Autowired;
import com.ysmart.annotation.Service;

@Service
public class StudentService {
    @Autowired
    private StudentDao studentDao;
    public void sayStudent(){
        System.out.println(studentDao.getStudent());
    }
}
