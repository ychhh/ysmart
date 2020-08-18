package com.ysmart.config;

import com.sun.deploy.config.ClientConfig;
import com.ysmart.enums.ResultCodeEnum;
import com.ysmart.exception.RRException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigUtil  配置工具类
 * @author 于成航 ychhh
 * @data 2020.8.15
 * @email 627387735@qq.com
 */
public class ConfigUtil {

    //配置文件信息类
    private static Properties properties;
    //构造方法
    public ConfigUtil() {
        properties=this.getproperties(null);
    }
    public ConfigUtil(String propertiesPath) {
        properties=this.getproperties(propertiesPath);
    }

    /**
     * 读取配置文件
     * @param propertiesPath
     * @return
     */
    private Properties getproperties(String propertiesPath) {
        InputStream in=null;
        if (propertiesPath==null||propertiesPath.length()!=0) {
            propertiesPath = "/application.properties";
        }
        Properties properties = new Properties();
        // 通过类的加载器获取具有给定名称的资源
        in = ConfigUtil.class.getResourceAsStream(propertiesPath);
        if (in==null){
            throw new RRException(ResultCodeEnum.PATH_ERROR.getDesc(), ResultCodeEnum.PATH_ERROR.getCode());
        }
        try {
//            System.out.println("正在加载配置文件");
            properties.load(in);
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }


    /**
     * 双重检测锁单例 获得instance
     * @return
     */
    public synchronized static Properties getInstance() {
        if (properties == null){
            synchronized (ConfigUtil.class){
                if (properties == null){
                    return new ClientConfig();
                }
            }
        }
        return properties;
    }

    /**
     * 根据配置文件的key获取value的值
     * @param propertiesKey
     * @return
     */
    public static Object getPropertiesByKey(String propertiesKey) {
        if (properties.size() > 0) {
            return properties.get(propertiesKey);
        }
        return null;
    }
}
