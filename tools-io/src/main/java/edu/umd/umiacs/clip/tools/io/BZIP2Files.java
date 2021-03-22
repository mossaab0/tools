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

import static edu.umd.umiacs.clip.tools.io.AllFiles.BUFFER_SIZE;
import static edu.umd.umiacs.clip.tools.io.AllFiles.asUncheckedRunnable;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import static java.nio.charset.CodingErrorAction.IGNORE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author Mossaab Bagdouri
 */
public class BZIP2Files {

    public static List<String> readAllLines(Path path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new BZip2CompressorInputStream(new BufferedInputStream(newInputStream(path), BUFFER_SIZE)), UTF_8.newDecoder().onMalformedInput(IGNORE)))) {
            List<String> result = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
            return result;
        }
    }

    protected static Stream<String> lines(Path path) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new BZip2CompressorInputStream(new BufferedInputStream(newInputStream(path), BUFFER_SIZE)), UTF_8.newDecoder().onMalformedInput(IGNORE)));
            return br.lines().onClose(asUncheckedRunnable(br));
        } catch (IOException e) {
            try {
                br.close();
            } catch (Exception ex) {
                try {
                    e.addSuppressed(ex);
                } catch (Throwable ignore) {
                }
            }
            throw e;
        }
    }

    protected static Stream<String> lines(File file) throws IOException {
        return lines(file.toPath());
    }

    protected static Stream<String> lines(String path) throws IOException {
        return lines(new File(path));
    }

    protected static Stream<CSVRecord> records(CSVFormat format, Path path) throws IOException {
        return StreamSupport.stream(format.parse(new BufferedReader(new InputStreamReader(new BZip2CompressorInputStream(new BufferedInputStream(newInputStream(path), BUFFER_SIZE)), UTF_8.newDecoder().onMalformedInput(IGNORE)))).spliterator(), false);
    }

    protected static Stream<CSVRecord> records(CSVFormat format, File file) throws IOException {
        return records(format, file.toPath());
    }

    protected static Stream<CSVRecord> records(CSVFormat format, String path) throws IOException {
        return records(format, new File(path));
    }

    protected static Path write(Path path, Iterable<? extends CharSequence> lines, OpenOption... options) throws IOException {
        return write(path, lines, UTF_8, options);
    }

    protected static Path write(Path path, Iterable<? extends CharSequence> lines,
            Charset cs, OpenOption... options)
            throws IOException {
        Objects.requireNonNull(lines);
        CharsetEncoder encoder = cs.newEncoder();
        OutputStream out = newOutputStream(path, options);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new BZip2CompressorOutputStream(out), encoder), BUFFER_SIZE)) {
            for (CharSequence line : lines) {
                writer.append(line);
                writer.newLine();
            }
        }
        return path;
    }

    protected static List<String> readAllLinesFromResource(String path) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader bis = new BufferedReader(new InputStreamReader(new BZip2CompressorInputStream(System.class.getResourceAsStream(path)), UTF_8.newDecoder().onMalformedInput(IGNORE)))) {
            String line;
            while ((line = bis.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
