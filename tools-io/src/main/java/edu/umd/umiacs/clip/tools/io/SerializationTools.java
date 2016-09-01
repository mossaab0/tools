/**
 * Tools IO
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
package edu.umd.umiacs.clip.tools.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

/**
 *
 * @author Mossaab Bagdouri
 */
public class SerializationTools {

    public static void serializeAndOverride(String path, Object object) {
        String tmp = path + "._SAVING";
        new File(tmp).delete();
        new File(tmp).getParentFile().mkdirs();

        try (ObjectOutputStream out = new ObjectOutputStream(new BZip2CompressorOutputStream(new FileOutputStream(tmp)))) {
            out.writeObject(object);
            new File(path).delete();
            new File(tmp).renameTo(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object deserialize(String path) {
        try (ObjectInputStream in = new ObjectInputStream(new BZip2CompressorInputStream(new FileInputStream(path)))) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
