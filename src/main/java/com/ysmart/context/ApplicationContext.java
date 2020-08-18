package com.ysmart.context;


import com.ysmart.annotation.*;
import com.ysmart.config.ConfigUtil;
import com.ysmart.exception.RRException;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.ysmart.util.ArrayUtil.useArrayUtils;
import static com.ysmart.config.ConfigUtil.getPropertiesByKey;
import static com.ysmart.util.StringConversionUtil.toLowercaseIndex;


/**
 * ApplicationContext ioc容器类
 * @author 于成航 ychhh
 * @data 2020.8.15
 * @email 627387735@qq.com
 */
public class ApplicationContext {

    /**
     * IOC容器 如： String(loginController) --> Object(loginController实例)
     */
    private Map<String, Object> iocBeanMap = new HashMap();
    /**
     * 类集合--存放所有的全限制类名
     */
    private Set<String> classSet = new HashSet();

    public ApplicationContext() throws InstantiationException, IllegalAccessException, ClassNotFoundException, ParseException {
        // 初始化数据
        this.classLoader();
    }

    /**
     * 从容器中获得获得bean
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {
        if (iocBeanMap != null) {
            return iocBeanMap.get(beanName);
        } else {
            return null;
        }
    }

    /**
     * 控制反转
     * @param classZ
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void addBeanToIoc(Class classZ) throws IllegalAccessException, InstantiationException {

        if (classZ.getAnnotation(Controller.class) != null) {
            iocBeanMap.put(toLowercaseIndex(classZ.getSimpleName()), classZ.newInstance());
//            System.out.println("控制反转访问控制层:" + toLowercaseIndex(classZ.getSimpleName()));
        } else if (classZ.getAnnotation(Service.class) != null) {
            // 将当前类交由IOC管理
            Service myService = (Service) classZ.getAnnotation(Service.class);
            iocBeanMap.put(toLowercaseIndex(classZ.getSimpleName()), classZ.newInstance());
//            System.out.println("控制反转服务层:" + toLowercaseIndex(classZ.getSimpleName()));
        } else if (classZ.getAnnotation(Mapping.class) != null) {
            Mapping myMapping = (Mapping) classZ.getAnnotation(Mapping.class);
            iocBeanMap.put(toLowercaseIndex(classZ.getSimpleName()), classZ.newInstance());
//            System.out.println("控制反转持久层:" + toLowercaseIndex(classZ.getSimpleName()));
        } else if (classZ.getAnnotation(Entity.class) !=null){
            Entity myEntity = (Entity) classZ.getAnnotation(Entity.class);
            iocBeanMap.put(toLowercaseIndex(classZ.getSimpleName()), classZ.newInstance());
//            System.out.println("控制反转实体类:" + toLowercaseIndex(classZ.getSimpleName()));
        }
    }


    /**
     * 依赖注入
     */
    private void addBeanToField(Object obj) throws IllegalAccessException, InstantiationException, ClassNotFoundException, ParseException {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Autowired.class) != null) {
                field.setAccessible(true);
                Autowired myAutowired = field.getAnnotation(Autowired.class);
                Class<?> fieldClass = field.getType();
                // 接口不能被实例化，需要对接口进行特殊处理获取其子类，获取所有实现类
                if (fieldClass.isInterface()) {
                    // 如果有指定获取子类名
                    if (myAutowired.value()!=null&&myAutowired.value().length()!=0) {
                        field.set(obj, iocBeanMap.get(myAutowired.value()));
                    } else {
                        // 当注入接口时，属性的名字与接口实现类名一致则直接从容器中获取
                        System.out.println("field.getName()"+field.getName());
                        Object objByName = iocBeanMap.get(field.getName());
                        if (objByName != null) {
                            field.set(obj, objByName);
                            // 递归依赖注入
                            addBeanToField(field.getType());
                        } else {
                            // 注入接口时，如果属性名称与接口实现类名不一致的情况下
                            List<Object> list = findSuperInterfaceByIoc(field.getType());
                            if (list != null && list.size() > 0) {
                                if (list.size() > 1) {
                                    throw new RuntimeException(obj.getClass() + "  注入接口 " + field.getType() + "   失败，请在注解中指定需要注入的具体实现类");
                                } else {
                                    field.set(obj, list.get(0));
                                    // 递归依赖注入
                                    addBeanToField(field.getType());
                                }
                            } else {
                                throw new RuntimeException("当前类" + obj.getClass() + "  不能注入接口 " + field.getType().getClass() + "  ， 接口没有实现类不能被实例化");
                            }
                        }
                    }
                } else {
                    String beanName = myAutowired.value()==null||myAutowired.value().length()==0 ? toLowercaseIndex(field.getName()) : toLowercaseIndex(myAutowired.value());
                    Object beanObj = iocBeanMap.get(beanName);
                    field.set(obj, beanObj == null ? field.getType().newInstance() : beanObj);
//                    System.out.println("依赖注入" + field.getName());
//                递归依赖注入
                }
                addBeanToField(field.getType());
            }
            if (field.getAnnotation(Value.class) != null) {
                field.setAccessible(true);
                Value value = field.getAnnotation(Value.class);
                setValueToFiled(obj,field,value.value());
            }

        }
    }

    /**
     * 向八大基本数据类型和String，date两个常用类注入值
     * @param obj
     * @param field
     * @param value
     * @throws IllegalAccessException
     * @throws ParseException
     */
    private void setValueToFiled(Object obj,Field field,String value) throws IllegalAccessException, ParseException {
        Class fieldType=field.getType();
        if (fieldType==byte.class){
            field.set(obj, Byte.parseByte(value));
        }
        if (fieldType==short.class){
            field.set(obj, Short.parseShort(value));
        }
        if (fieldType==int.class){
            field.set(obj,Integer.parseInt(value));
        }
        if (fieldType==long.class){
            field.set(obj, Long.parseLong(value));
        }
        if (fieldType==boolean.class){
            field.set(obj, Boolean.parseBoolean(value));
        }
        if (fieldType==char.class){
            field.set(obj, Character.valueOf(value.charAt(0)));
        }
        if (fieldType==float.class){
            field.set(obj, Float.parseFloat(value));
        }
        if (fieldType==double.class){
            field.set(obj, Double.parseDouble(value));
        }
        if (fieldType==String.class){
            field.set(obj, value);
        }
        if (fieldType==Date.class){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(value);
            field.set(obj,date);
        }
    }
    /**
     判断需要注入的接口所有的实现类
     */
    private List<Object> findSuperInterfaceByIoc(Class classz) {
        Set<String> beanNameList = iocBeanMap.keySet();
        ArrayList<Object> objectArrayList = new ArrayList<>();
        for (String beanName : beanNameList) {
            Object obj = iocBeanMap.get(beanName);
            Class<?>[] interfaces = obj.getClass().getInterfaces();
            if (useArrayUtils(interfaces, classz)) {
                objectArrayList.add(obj);
            }
        }
        return objectArrayList;
    }

    /**
     类加载器
     */
    private void classLoader() throws ClassNotFoundException, InstantiationException, IllegalAccessException, ParseException {
        // 加载配置文件所有配置信息
        new ConfigUtil();
        // 获取扫描包路径
        String classScanPath = (String) ConfigUtil.getInstance().get("ioc.scan.path");
        if (classScanPath!=null&&classScanPath.length()!=0) {
            classScanPath = classScanPath.replace(".", "/");
        } else {
            throw new RuntimeException("请配置项目包扫描路径 ioc.scan.path");
        }

        getPackageClassFile(classScanPath);
        for (String className : classSet) {
            addBeanToIoc(Class.forName(className));
        }
        // 获取带有Service注解类的所有的带Autowired注解的属性并对其进行实例化
        Set<String> beanKeySet = iocBeanMap.keySet();
        for (String beanName : beanKeySet) {
            addBeanToField(iocBeanMap.get(beanName));
        }
    }


    /**
     * 扫描项目根目录中所有的class文件
     * @param packageName
     */
    private void getPackageClassFile(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName);
        File file=null;
        try {
            file = new File(url.getFile());
        }
        catch (Exception e){
            throw new RuntimeException("没有读取到改文件夹中的内容,请检查文件目录");
        }
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fileSon : files) {
                if (fileSon.isDirectory()) {
                    // 递归扫描
                    getPackageClassFile(packageName + "/" + fileSon.getName());
                } else {
                    // 是文件并且是以 .class结尾
                    if (fileSon.getName().endsWith(".class")) {
//                        System.out.println("正在加载: " + packageName.replace("/", ".") + "." + fileSon.getName());
                        classSet.add(packageName.replace("/", ".") + "." + fileSon.getName().replace(".class", ""));
                    }
                }
            }
        } else {
            throw new RuntimeException("没有找到需要扫描的文件目录");
        }
    }



}
