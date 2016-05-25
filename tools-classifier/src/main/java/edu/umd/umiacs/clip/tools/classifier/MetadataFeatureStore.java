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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.util.stream.Stream;

/**
 *
 * @author Mossaab Bagdouri
 */
public class MetadataFeatureStore {

    private final Map<String, Integer> features = new HashMap<>();

    public int size() {
        return features.size();
    }

    public Integer getFeatureId(String key) {
        if (!features.containsKey(key)) {
            features.put(key, features.size() + 1);
        }
        return features.get(key);
    }

    public String getParsed(AdditiveMap map) {
        return LibSVMUtils.asString(map.entrySet().stream().
                collect(toMap(entry -> getFeatureId(entry.getKey()), Entry::getValue)));
    }

    public String getParsed(String rawFeatures) {
        rawFeatures = rawFeatures.replaceAll("\\s+", " ").trim();
        if (rawFeatures.isEmpty()) {
            return "";
        } else {
            return LibSVMUtils.asString(Stream.of(rawFeatures.split(" ")).
                    map(pair -> pair.split(":")).
                    collect(toMap(pair -> getFeatureId(pair[0]), pair -> new Double(pair[1]))));
        }
    }

    public List<String> getParsed(List<String> rawFeaturesList) {
        return rawFeaturesList.stream().map(this::getParsed).collect(toList());
    }

    public List<String> getParsedMaps(List<AdditiveMap> rawFeaturesList) {
        return rawFeaturesList.stream().map(this::getParsed).collect(toList());
    }
}
