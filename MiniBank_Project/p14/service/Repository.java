package service;

import model.annotation.Id;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Practical 13 - one generic repository that can store any type T.
 * The key of each object is read, by reflection, from the field marked @Id.
 */
public class Repository<T> {

    private final HashMap<String, T> items = new HashMap<>();

    // Methods are synchronized because the service and batch threads share it.
    public synchronized T save(T entity) {
        items.put(idOf(entity), entity);
        return entity;
    }

    @SafeVarargs
    public final void saveAll(T... entities) {
        for (T entity : entities) {
            save(entity);
        }
    }

    public synchronized T findById(String id) {
        return items.get(id);
    }

    public synchronized ArrayList<T> findAll() {
        return new ArrayList<>(items.values());
    }

    public synchronized boolean delete(String id) {
        return items.remove(id) != null;
    }

    public synchronized int count() {
        return items.size();
    }

    // ---- reflection: find the @Id field and read its value ----

    private String idOf(T entity) {
        Field idField = findIdField(entity.getClass());
        try {
            return String.valueOf(idField.get(entity));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot read @Id field", e);
        }
    }

    private static Field findIdField(Class<?> type) {
        // Look in the class itself and then in every superclass.
        for (Class<?> c = type; c != null && c != Object.class;
             c = c.getSuperclass()) {

            for (Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    return field;
                }
            }
        }
        throw new IllegalArgumentException(
                type.getSimpleName() + " has no field marked @Id");
    }

    // ---- generic helper methods ----

    // Bounded type parameter: only Integer, Double, Long ... are accepted.
    public static <N extends Number> double average(N[] values) {
        if (values.length == 0) {
            return 0.0;
        }
        double sum = 0;
        for (N value : values) {
            sum += value.doubleValue();
        }
        return sum / values.length;
    }

    // Wildcard: accepts a collection of any Number subtype.
    public static double total(Collection<? extends Number> numbers) {
        double sum = 0;
        for (Number number : numbers) {
            sum += number.doubleValue();
        }
        return sum;
    }
}
