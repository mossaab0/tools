/**
 * Tools Classifier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.umd.umiacs.clip.tools.classifier;

import java.util.HashMap;

/**
 *
 * @author Mossaab Bagdouri
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
