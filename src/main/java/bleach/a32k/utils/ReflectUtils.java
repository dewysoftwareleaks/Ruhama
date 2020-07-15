package bleach.a32k.utils;

import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ReflectUtils
{
    public static Field getField(Class<?> c, String... names)
    {
        int l = names.length;
        int fernflowerSucks = 0;

        while (fernflowerSucks < l)
        {
            String s = names[fernflowerSucks];

            try
            {
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                Field f = c.getDeclaredField(s);

                f.setAccessible(true);
                modifiersField.setInt(f, f.getModifiers() & -17);

                return f;
            } catch (Exception e)
            {
                ++fernflowerSucks;
            }
        }

        System.out.println("Invalid Fields: " + Arrays.asList(names) + " For Class: " + c.getName());
        return null;
    }

    public static Object callMethod(Object target, Object[] params, String... names)
    {
        int len = names.length;
        int i = 0;

        while (i < len)
        {
            String s = names[i];

            try
            {
                return MethodUtils.invokeMethod(target, true, s, params);
            } catch (Exception e)
            {
                ++i;
            }
        }

        System.out.println("Invalid Method: " + Arrays.asList(names));

        return null;
    }
}
