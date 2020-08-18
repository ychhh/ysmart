import com.student.StudentService;
import com.ysmart.context.ApplicationContext;

import java.text.ParseException;

public class test {


    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, ParseException, IllegalAccessException {
        ApplicationContext applicationContext=new ApplicationContext();
        StudentService studentService = (StudentService) applicationContext.getBean("studentService");
        studentService.sayStudent();
    }
}
