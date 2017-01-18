package com.yao.devsdk.utils;

import android.support.annotation.Nullable;

import com.yao.devsdk.log.LogUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Java Reflection Cookbook
 */
public class ReflectionUtil {
    private static final String TAG = "ReflectionUtils";


    /**
     * 循环向上转型, 获取对象的 DeclaredMethod
     *
     * @param clazz    : 查找方法的类
     * @param methodName     : 父类中的方法名
     * @param parameterTypes : 父类中的方法参数类型
     * @return 父类中的方法对象
     */

    public static @Nullable Method getDeclaredMethod(Class clazz, String methodName, Class<?>... parameterTypes) {
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
                return method;
            } catch (NoSuchMethodException e) {
                //这里必须捕获异常，不能抛出去。
                //否则就不会执行clazz = clazz.getSuperclass(),不会进入到父类中
                LogUtil.i(TAG,clazz.getSimpleName()+" 中没有找到方法 "+methodName);
            }
        }

        return null;
    }

    /**
     * 直接调用对象方法, 而忽略修饰符(private, protected, default)
     *
     * @param object         : 子类对象
     * @param methodName     : 父类中的方法名
     * @param parameterTypes : 参数类型，支持基本类型 例如:int为int.class
     * @param parameters     : 父类中的方法参数
     * @return 父类中方法的执行结果
     */

    public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes,
                                      Object[] parameters) {
        try {

            //根据 对象、方法名和对应的方法参数 通过反射 调用上面的方法获取 Method 对象
            Method method = getDeclaredMethod(object.getClass(), methodName, parameterTypes);

            if (method != null) {
                boolean oldAccessible = method.isAccessible();
                //抑制Java对方法进行检查,主要是针对私有方法而言
                method.setAccessible(true);
                //调用object 的 method 所代表的方法，其方法的参数是 parameters
                Object obj = method.invoke(object, parameters);
                method.setAccessible(oldAccessible);
                return obj;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "反射异常", e);
        }

        return null;
    }


    /**
     * 直接调用对象方法, 参数中不含基本类型方法
     */
    public static Object invokeMethodWithoutBasicParams(Object object, String methodName, Object[] parameters) {
        try {
            Class[] parameterTypes = new Class[parameters.length];

            for (int i = 0, j = parameters.length; i < j; i++) {
                parameterTypes[i] = parameters[i].getClass();
            }

            Object returnValue = invokeMethod(object, methodName, parameterTypes, parameters);
            return returnValue;

        } catch (Exception e) {
            LogUtil.e(TAG, "反射异常", e);
        }

        return null;
    }

    /**
     * 循环向上转型, 获取对象的 DeclaredField
     *
     * @param clazz     : 在此类中查找字段
     * @param fieldName : 父类中的属性名
     * @return 父类中的属性对象
     */

    public static Field getDeclaredField(Class clazz, String fieldName) {

        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                return field;
            } catch (NoSuchFieldException e) {
                //这里必须捕获异常，不能抛出去。
                //否则就不会执行clazz = clazz.getSuperclass(),不会进入到父类中
                LogUtil.i(TAG,clazz.getSimpleName()+" 中没有找到字段 "+fieldName);
            }
        }

        return null;
    }

    /**
     * 直接设置对象属性值, 忽略 private/protected 修饰符, 也不经过 setter
     *
     * @param object    : 子类对象
     * @param fieldName : 父类中的属性名
     * @param value     : 将要设置的值
     */

    public static void setFieldValue(Object object, String fieldName, Object value) {
        try {
            //根据 对象和属性名通过反射 调用上面的方法获取 Field对象
            Field field = getDeclaredField(object.getClass(), fieldName);
            if (field != null) {
                boolean oldAccessible = field.isAccessible();
                //抑制Java对其的检查
                field.setAccessible(true);
                //将 object 中 field 所代表的值 设置为 value
                field.set(object, value);
                field.setAccessible(oldAccessible);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "反射异常", e);
        }

    }

    /**
     * 直接读取对象的属性值, 忽略 private/protected 修饰符, 也不经过 getter
     *
     * @param object    : 子类对象
     * @param fieldName : 父类中的属性名
     * @return : 父类中的属性值
     */

    public static Object getFieldValue(Object object, String fieldName) {


        try {
            //根据 对象和属性名通过反射 调用上面的方法获取 Field对象
            Field field = getDeclaredField(object.getClass(), fieldName);
            if (field != null) {
                boolean oldAccessible = field.isAccessible();
                //抑制Java对其的检查
                field.setAccessible(true);
                //获取 object 中 field 所代表的属性值
                Object obj = field.get(object);
                field.setAccessible(oldAccessible);
                return obj;
            }

        } catch (Exception e) {
            LogUtil.e(TAG, "反射异常", e);
        }

        return null;
    }


    /**
     * 获取public的静态字段值
     * 非pulic静态字段不能被反射到
     * 静态字段属于类，所以没有继承关系，子类中有所有父类的静态字段
     * @param clazz
     * @param fieldName
     * @return
     * @throws Exception
     */
    public static Object getStaticProperty(Class<?> clazz, String fieldName) {
        try {
            Field field = getDeclaredField(clazz, fieldName);
            if (field!=null){
                boolean oldAccessible = field.isAccessible();
                field.setAccessible(true);
                Object obj = field.get(null);
                field.setAccessible(oldAccessible);
                return obj;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "反射异常", e);
        }

        return null;
    }


    /**
     * 反射调用静态方法,参数中不能含有基本类型
     *
     * @param clazz      类
     * @param methodName 静态方法名
     * @param args       静态方法参数
     * @return
     */
    public static Object invokeStaticMethodWithoutBasicParams(Class clazz, String methodName,
                                                              Object[] args) {
        try {
            Class[] argsClass = new Class[args.length];
            for (int i = 0, j = args.length; i < j; i++) {
                argsClass[i] = args[i].getClass();
            }

            return invokeStaticMethod(clazz, methodName, argsClass, args);

        } catch (Exception e) {
            LogUtil.e(TAG, "反射异常", e);
        }
        return null;
    }


    /**
     * 反射调用静态方法
     *
     * @param clazz             类
     * @param methodName        静态方法名
     * @param parameterTypes    静态方法参数类型
     * @param parameters        静态方法参数
     * @return
     */
    public static Object invokeStaticMethod(Class clazz, String methodName, Class<?>[] parameterTypes,
                                            Object[] parameters) {
        try {
            Method method = getDeclaredMethod(clazz, methodName, parameterTypes);
            if (method!=null){
                boolean oldAccessible = method.isAccessible();
                method.setAccessible(true);
                Object obj = method.invoke(null, parameters);
                method.setAccessible(oldAccessible);
                return obj;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "反射异常", e);
        }
        return null;
    }



    /**
     * 创建一个类的对象
     * 参数不能为基本类型
     */
    public static Object newInstanceWithoutBasicParams(Class clazz, Object[] args) {

        try {
            Class[] argsClass = new Class[args.length];
            for (int i = 0, j = args.length; i < j; i++) {
                argsClass[i] = args[i].getClass();
            }

            return newInstance(clazz, argsClass, args);

        } catch (Exception e) {
            LogUtil.e(TAG, "反射异常", e);
        }

        return null;

    }
    /**
     * 创建一个类的对象
     *
     * @param clazz              类
     * @param parameterTypes     参数类型
     * @param args               参数数组
     * @return
     */
    public static Object newInstance(Class<?> clazz, Class<?>[] parameterTypes, Object[] args) {
        try {
            Constructor cons = clazz.getDeclaredConstructor(parameterTypes);
            boolean oldAccessible = cons.isAccessible();
            cons.setAccessible(true);
            Object obj = cons.newInstance(args);
            cons.setAccessible(oldAccessible);
            return obj;

        } catch (Exception e) {
            LogUtil.e(TAG, "反射异常", e);
        }

        return null;

    }

    /**
     * @param obj
     * @param cls
     * @return
     */
    public static boolean isInstance(Object obj, Class cls) {
        return cls.isInstance(obj);
    }

    /**
     * @param array
     * @param index
     * @return
     */
    public Object getByArray(Object array, int index) {
        return Array.get(array, index);
    }



}
