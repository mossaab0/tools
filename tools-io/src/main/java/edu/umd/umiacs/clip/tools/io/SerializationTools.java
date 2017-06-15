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
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

/**
 *
 * @author Mossaab Bagdouri
 */
public class SerializationTools {

    public static void serializeAndOverride(String path, Object object) {
        String tmp = path + "._SAVING";
        new File(tmp).delete();
        new File(tmp).getParentFile().mkdirs();
        try (FileOutputStream os = new FileOutputStream(tmp);
                ObjectOutputStream out = new ObjectOutputStream(
                        path.endsWith(".bz2") ? new BZip2CompressorOutputStream(os)
                        : path.endsWith(".gz") ? new GzipCompressorOutputStream(os)
                        : os)) {
            out.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new File(path).delete();
        new File(tmp).renameTo(new File(path));
    }

    public static Object deserialize(String path) {

        try (FileInputStream is = new FileInputStream(path);
                ObjectInputStream in = new ObjectInputStream(
                        path.endsWith(".bz2") ? new BZip2CompressorInputStream(is)
                        : path.endsWith(".gz") ? new GzipCompressorInputStream(is)
                        : is)) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
