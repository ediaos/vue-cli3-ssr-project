package com.terwergreen.jvue.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Terwer
 * @Date 2018/11/30 10:32
 * @Version 1.0
 * @Description 反射工具类
 **/
public class ReflectUtil {
    private static final Log logger = LogFactory.getLog(ReflectUtil.class);

    /**
     * 判断对象是否实现接口或者继承了某个类，只支持继承自一级的情况，即B extends A或者B implements A的情况
     *
     * @param clazz1
     * @param clazz2
     * @return
     */
    public static boolean instanceOf(Class<?> clazz1, Class<?> clazz2) {
        // 比较自己类型
        if (clazz1.getTypeName().equals(clazz2.getTypeName())) {
            return true;
        }
        // 比较自己和父类的类型
        return clazz1.getSuperclass().getTypeName().equals(clazz2.getTypeName());
    }

    /**
     * 获取所有的父类
     *
     * @param clazz
     * @return
     */
    public static List<Class<?>> getSuperclass(Class<?> clazz) {
        List<Class<?>> superClassList = new ArrayList<>();
        Class superClass = clazz.getSuperclass();
        while (superClass != null) {
            superClassList.add(superClass);
            superClass = superClass.getSuperclass();
        }
        return superClassList;
    }

    /**
     * 创建新对象
     *
     * @param clazz
     * @return
     */
    public static Object newInstance(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            logger.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 反射读取构造方法创建新对象
     *
     * @param clazz
     * @param initargs 构造函数参数
     * @return
     */
    public static Object newInstance(Class<?> clazz, Object... initargs) {
        Constructor<?> constructor = null;
        try {
            Class[] initargTypes = new Class[initargs.length];
            for (int i = 0; i < initargs.length; i++) {
                Object initarg = initargs[i];
                initargTypes[i] = initarg.getClass();
            }
            constructor = clazz.getDeclaredConstructor(initargTypes);
            //设置允许访问，防止private修饰的构造方法
            constructor.setAccessible(true);
            return constructor.newInstance(initargs);
        } catch (NoSuchMethodException e) {
            logger.error("Class " + clazz + "newInstance fail", e);
        } catch (IllegalAccessException e) {
            logger.error("Class " + clazz + "newInstance fail", e);
        } catch (InstantiationException e) {
            logger.error("Class " + clazz + "newInstance fail", e);
        } catch (InvocationTargetException e) {
            logger.error("Class " + clazz + "newInstance fail", e);
        }
        return null;
    }

    /**
     * 反射读取构造方法创建新对象，自定义所有参数类型
     *
     * @param clazz
     * @param initargs 构造函数参数
     * @return
     */
    public static Object newInstance(Class<?> clazz, Class[] initargTypes, Object[] initargs) {
        Constructor<?> constructor = null;
        try {
            constructor = clazz.getDeclaredConstructor(initargTypes);
            //设置允许访问，防止private修饰的构造方法
            constructor.setAccessible(true);
            return constructor.newInstance(initargs);
        } catch (NoSuchMethodException e) {
            logger.error("Class " + clazz + "newInstance fail", e);
        } catch (IllegalAccessException e) {
            logger.error("Class " + clazz + "newInstance fail", e);
        } catch (InstantiationException e) {
            logger.error("Class " + clazz + "newInstance fail", e);
        } catch (InvocationTargetException e) {
            logger.error("Class " + clazz + "newInstance fail", e);
        }
        return null;
    }

    /**
     * 反射读取属性
     *
     * @param obj
     * @param fieldName
     * @return
     */
    public static Object getField(Object obj, String fieldName) {
        Class<?> clazz = obj.getClass();
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);//为true则表示反射的对象在使用时取消Java语言访问检查
            return field.get(obj);
        } catch (NoSuchFieldException e) {
            logger.error("Class " + obj + " get field" + fieldName + " fail", e);
        } catch (IllegalAccessException e) {
            logger.error("Class " + obj + " get field" + fieldName + " fail", e);
        }
        return null;
    }

    /**
     * 反射设置属性
     *
     * @param obj
     * @param fieldName
     * @param value
     */
    public static void setField(Object obj, String fieldName, Object value) {
        Class<?> clazz = obj.getClass();
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);//为true则表示反射的对象在使用时取消Java语言访问检查
            field.set(obj, value);
        } catch (NoSuchFieldException e) {
            logger.error("Class " + obj + " set field" + fieldName + " fail", e);
        } catch (IllegalAccessException e) {
            logger.error("Class " + obj + " set field" + fieldName + " fail", e);
        }
    }

    /**
     * 反射调用无参方法
     *
     * @param obj
     * @param methodName
     * @return
     */
    public static Object invoke(Object obj, String methodName) {
        try {
            Class<?> clazz = obj.getClass();
            Method method = clazz.getDeclaredMethod(methodName);
            method.setAccessible(true);//为true则表示反射的对象在使用时取消Java语言访问检查
            return method.invoke(obj);
        } catch (NoSuchMethodException e) {
            logger.error("Class " + obj + " invoke function" + methodName + " fail", e);
        } catch (IllegalAccessException e) {
            logger.error("Class " + obj + " invoke function" + methodName + " fail", e);
        } catch (InvocationTargetException e) {
            logger.error("Class " + obj + " invoke function" + methodName + " fail", e);
        }
        return null;
    }

    /**
     * 反射调用有参方法
     *
     * @param obj
     * @param methodName
     * @param args
     * @return
     */
    public static Object invoke(Object obj, String methodName, Object... args) {
        try {
            Class<?> clazz = obj.getClass();
            Class[] argTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                argTypes[i] = arg.getClass();
            }
            Method method = clazz.getDeclaredMethod(methodName, argTypes);
            method.setAccessible(true);//为true则表示反射的对象在使用时取消Java语言访问检查
            return method.invoke(obj, args);
        } catch (NoSuchMethodException e) {
            logger.error("Class " + obj + " invoke function" + methodName + " fail", e);
        } catch (IllegalAccessException e) {
            logger.error("Class " + obj + " invoke function" + methodName + " fail", e);
        } catch (InvocationTargetException e) {
            logger.error("Class " + obj + " invoke function" + methodName + " fail", e);
        }
        return null;
    }

    /**
     * 反射调用有参方法，自定义所有参数类型
     *
     * @param obj
     * @param methodName
     * @param args
     * @return
     */
    public static Object invoke(Object obj, String methodName, Class[] argTypes, Object[] args) {
        try {
            Class<?> clazz = obj.getClass();
            Method method = clazz.getDeclaredMethod(methodName, argTypes);
            method.setAccessible(true);//为true则表示反射的对象在使用时取消Java语言访问检查
            return method.invoke(obj, args);
        } catch (NoSuchMethodException e) {
            logger.error("Class " + obj + " invoke function" + methodName + " fail", e);
        } catch (IllegalAccessException e) {
            logger.error("Class " + obj + " invoke function" + methodName + " fail", e);
        } catch (InvocationTargetException e) {
            logger.error("Class " + obj + " invoke function" + methodName + " fail", e);
        }
        return null;
    }
}