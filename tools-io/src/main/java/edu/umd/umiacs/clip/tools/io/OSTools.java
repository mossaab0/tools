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

import static edu.umd.umiacs.clip.tools.io.AllFiles.write;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.List;

/**
 *
 * @author Mossaab Bagdouri
 */
public class OSTools {

    private static final boolean DEBUG = false;

    public static List<Integer> getProcessIds(String st) {
        List<Integer> list = new ArrayList<>();
        Runtime runtime = Runtime.getRuntime();
        try {
            Process proc = runtime.exec("ps -eaf");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains(st)) {
                        line = line.substring(line.indexOf(" ")).trim();
                        line = line.substring(0, line.indexOf(" ")).trim();
                        list.add(new Integer(line));
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void exec(String cmd) {
        if (DEBUG) {
            System.out.println(cmd);
        }
        File file = new File("/tmp/script-" + new java.util.Random().nextInt(Integer.MAX_VALUE) + ".sh");
        file.getParentFile().mkdirs();
        file.delete();
        file.deleteOnExit();
        try {
            write(file, asList("#!/bin/bash\n", cmd));
            file.setExecutable(true);
            Process proc = Runtime.getRuntime().exec(file.getAbsolutePath());
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (DEBUG) {
                        System.err.println(line);
                    }
                }
            }
            proc.waitFor();
            proc.destroy();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
