package edu.umd.umiacs.clip.tools.classifier;

import java.util.HashMap;

/**
 *
 * @author mossaab
 */
public class AdditiveMap extends HashMap<String, Double> {

    public void add(String key, Object value) {
        add(key, value, 1);
    }

    public void add(String key, Object value, double weight) {
        double old = getOrDefault(value, 0d);
        if (value instanceof Boolean) {
            if (((Boolean) value) && old + weight != 0) {
                put(key, old + weight);
            }
        } else {
            put(key, old + weight * ((Number) value).doubleValue());
            if (get(key) == 0) {
                remove(key);
            }
        }
    }

}
