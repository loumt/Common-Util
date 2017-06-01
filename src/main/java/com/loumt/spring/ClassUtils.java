/**
 * Copyright (c) www.bugull.com
 */
package com.loumt.spring;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

/**
 * USED TO:
 * Log File:
 *
 * @author loumt(loumt@sanlogic.com)
 * @project Common-Util
 * @package com.loumt.spring
 * @date 2017/5/24/024
 */
public class ClassUtils {

    public static final String ARRAY_SUFFIX = "[]";
    private static final String INTERNAL_ARRAY_PREFIX = "[";
    private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";
    private static final char PACKAGE_SEPARATOR = '.';
    private static final char INNER_CLASS_SEPARATOR = '$';
    public static final String CGLIB_CLASS_SEPARATOR = "$$";
    public static final String CLASS_FILE_SUFFIX = ".class";
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new HashMap(8);
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new HashMap(8);
    private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap(32);
    private static final Map<String, Class<?>> commonClassCache = new HashMap(32);


    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;

        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable var1) {
            ;
        }

        if(cl == null) {
            cl = ClassUtils.class.getClassLoader();
        }

        return cl;
    }

    public static Class<?> resolvePrimitiveClassName(String name) {
        Class result = null;
        if(name != null && name.length() <= 8) {
            result = (Class)primitiveTypeNameMap.get(name);
        }

        return result;
    }


    public static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
        Assert.notNull(name, "Name must not be null");
        Class clazz = resolvePrimitiveClassName(name);
        if(clazz == null) {
            clazz = (Class)commonClassCache.get(name);
        }

        if(clazz != null) {
            return clazz;
        } else {
            Class ex;
            String classLoaderToUse1;
            if(name.endsWith("[]")) {
                classLoaderToUse1 = name.substring(0, name.length() - "[]".length());
                ex = forName(classLoaderToUse1, classLoader);
                return Array.newInstance(ex, 0).getClass();
            } else if(name.startsWith("[L") && name.endsWith(";")) {
                classLoaderToUse1 = name.substring("[L".length(), name.length() - 1);
                ex = forName(classLoaderToUse1, classLoader);
                return Array.newInstance(ex, 0).getClass();
            } else if(name.startsWith("[")) {
                classLoaderToUse1 = name.substring("[".length());
                ex = forName(classLoaderToUse1, classLoader);
                return Array.newInstance(ex, 0).getClass();
            } else {
                ClassLoader classLoaderToUse = classLoader;
                if(classLoader == null) {
                    classLoaderToUse = getDefaultClassLoader();
                }

                try {
                    return classLoaderToUse.loadClass(name);
                } catch (ClassNotFoundException var8) {
                    int lastDotIndex = name.lastIndexOf(46);
                    if(lastDotIndex != -1) {
                        String innerClassName = name.substring(0, lastDotIndex) + '$' + name.substring(lastDotIndex + 1);

                        try {
                            return classLoaderToUse.loadClass(innerClassName);
                        } catch (ClassNotFoundException var7) {
                            ;
                        }
                    }

                    throw var8;
                }
            }
        }
    }
}
