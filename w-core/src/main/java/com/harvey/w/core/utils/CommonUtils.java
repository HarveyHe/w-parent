package com.harvey.w.core.utils;

public class CommonUtils {

    /**
     * like oracle decode function
     * 
     * @param value 返回值
     * @param args 判断参数
     * @return 返回条件成立的参数
     */
    public static Object decode(Object value, Object... args) {
        int i = 0;
        while (true) {
            if ((value == null && args[i] == null) || value.equals(args[i])) {
                return args[i + 1];
            } else {
                if (args.length == i + 3) {
                    // default
                    return args[i + 2];
                } else {
                    i = i + 2;
                    continue;
                }
            }
        }
    }

    /**
     * like mysql ifnull function
     * 
     * @param args 判断参数数组
     * @return 返回非空的参数
     */
    public static Object ifnull(Object... args) {
        for (Object arg : args) {
            if (arg != null) {
                return arg;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T of(Object inst, Class<T> clazz) {
        if (inst == null || clazz == null || !clazz.isInstance(inst)) {
            return null;
        }
        return (T) inst;
    }
    
}
