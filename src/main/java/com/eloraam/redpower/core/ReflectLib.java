package com.eloraam.redpower.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectLib {
    public static void
    callClassMethod(String className, String method, Class[] def, Object... params) {
        Class cl;
        try {
            cl = Class.forName(className);
        } catch (ClassNotFoundException var9) {
            return;
        }

        Method mth;
        try {
            mth = cl.getDeclaredMethod(method, def);
        } catch (NoSuchMethodException var8) {
            return;
        }

        try {
            mth.invoke(null, params);
        } catch (InvocationTargetException | IllegalAccessException var7) {}
    }

    public static <T>
        T getStaticField(String classname, String var, Class<? extends T> varcl) {
        Class cl;
        try {
            cl = Class.forName(classname);
        } catch (ClassNotFoundException var9) {
            return null;
        }

        Field fld;
        try {
            fld = cl.getDeclaredField(var);
        } catch (NoSuchFieldException var8) {
            return null;
        }

        Object ob;
        try {
            ob = fld.get(null);
        } catch (NullPointerException | IllegalAccessException var7) {
            return null;
        }

        return (T) (!varcl.isInstance(ob) ? null : ob);
    }

    public static <T> T getField(Object ob, String var, Class<? extends T> varcl) {
        Class cl = ob.getClass();

        Field fld;
        try {
            fld = cl.getDeclaredField(var);
        } catch (NoSuchFieldException var8) {
            return null;
        }

        Object ob2;
        try {
            ob2 = fld.get(ob);
        } catch (NullPointerException | IllegalAccessException var7) {
            return null;
        }

        return (T) (!varcl.isInstance(ob2) ? null : ob2);
    }
}
