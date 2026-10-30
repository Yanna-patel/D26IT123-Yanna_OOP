package util;

import model.annotation.MaxLength;
import model.annotation.Positive;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Reads the custom annotations of any object by reflection and reports the
 * rules that are broken (Practical 7 idea, used again by Practical 13).
 */
public class AnnotationValidator {

    // Returns only the messages, e.g. {"must be > 0"}.
    public static String[] validate(Object obj) {
        return check(obj, false);
    }

    // Same check, but every message starts with the field name.
    public static String[] validateWithFieldNames(Object obj) {
        return check(obj, true);
    }

    private static String[] check(Object obj, boolean withFieldName) {

        ArrayList<String> errors = new ArrayList<>();

        // Walk up the class hierarchy: a SavingsAccount object keeps its
        // annotated fields (balance, accountNumber) in the Account superclass.
        for (Class<?> type = obj.getClass();
             type != null && type != Object.class;
             type = type.getSuperclass()) {

            for (Field field : type.getDeclaredFields()) {

                field.setAccessible(true);

                try {
                    Object value = field.get(obj);

                    if (field.isAnnotationPresent(Positive.class)
                            && value instanceof Number number
                            && number.doubleValue() <= 0) {

                        String message =
                                field.getAnnotation(Positive.class).message();
                        errors.add(withFieldName
                                ? field.getName() + ": " + message
                                : message);
                    }

                    if (field.isAnnotationPresent(MaxLength.class)
                            && value instanceof String text) {

                        int max = field.getAnnotation(MaxLength.class).value();

                        if (text.length() > max) {
                            String message = "length must be <= " + max;
                            errors.add(withFieldName
                                    ? field.getName() + ": " + message
                                    : message);
                        }
                    }

                } catch (IllegalAccessException e) {
                    throw new IllegalStateException(
                            "Cannot read field " + field.getName(), e);
                }
            }
        }

        return errors.toArray(new String[0]);
    }
}
