package bleach.a32k.utils;

import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ReflectUtils
{
    public static Field getField(Class<?> c, String... names)
    {
        String[] var2 = names;
        int var3 = names.length;
        int var4 = 0;

        while (var4 < var3)
        {
            String s = var2[var4];

            try
            {
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                Field f = c.getDeclaredField(s);
                f.setAccessible(true);
                modifiersField.setInt(f, f.getModifiers() & -17);
                return f;
            } catch (Exception var8)
            {
                ++var4;
            }
        }

        System.out.println("Invalid Fields: " + Arrays.asList(names) + " For Class: " + c.getName());
        return null;
    }

    public static Object callMethod(Object target, Object[] params, String... names)
    {
        String[] var3 = names;
        int var4 = names.length;
        int var5 = 0;

        while (var5 < var4)
        {
            String s = var3[var5];

            try
            {
                return MethodUtils.invokeMethod(target, true, s, params);
            } catch (Exception var8)
            {
                ++var5;
            }
        }

        System.out.println("Invalid Method: " + Arrays.asList(names));
        return null;
    }
}
