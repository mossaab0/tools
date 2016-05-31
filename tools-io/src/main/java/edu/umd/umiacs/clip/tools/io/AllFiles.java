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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import static java.nio.charset.CodingErrorAction.IGNORE;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

/**
 *
 * @author Mossaab Bagdouri
 */
public class AllFiles {

    public static final int BUFFER_SIZE = 1024 * 1024;
    public static final boolean REMOVE_OLD_FILE = true;

    private static List<String> _readAllLines(Path path) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(newInputStream(path), BUFFER_SIZE), UTF_8.newDecoder().onMalformedInput(IGNORE)))) {
            List<String> result = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
            return result;
        }
    }

    public static List<String> readAllLines(Path path) {
        try {
            return path.toString().endsWith(".gz") ? GZIPFiles.readAllLines(path)
                    : path.toString().endsWith(".bz2") ? BZIP2Files.readAllLines(path)
                    : _readAllLines(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static List<String> readAllLines(File file) {
        return readAllLines(file.toPath());
    }

    private static String format(String path){
        if (path.startsWith("~" + File.separator)) {
            path = System.getProperty("user.home") + path.substring(1);
        }
        return path;
    }
    
    public static List<String> readAllLines(String path) {
        File file = new File(format(path));
        return !path.contains("*") ? readAllLines(file)
                : Stream.of(file.getParentFile()
                        .listFiles((dir, name) -> name.matches(file.getName().replace(".", "\\.").replace("*", ".+"))))
                .sorted()
                .map(AllFiles::readAllLines)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    public static List<String> readAllLinesFromResource(String path) {
        path = format(path);
        return path.endsWith(".gz") ? GZIPFiles.readAllLinesFromResource(path)
                : path.endsWith(".bz2") ? BZIP2Files.readAllLinesFromResource(path)
                : readAllLines(System.class.getResourceAsStream(path));
    }

    public static List<String> readAllLines(InputStream is) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader bis = new BufferedReader(new InputStreamReader(is, UTF_8.newDecoder().onMalformedInput(IGNORE)))) {
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

    protected static Runnable asUncheckedRunnable(Closeable c) {
        return () -> {
            try {
                c.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    public static Stream<File> list(Path dir) {
        try {
            return Files.list(dir).map(Path::toFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Stream<File> list(File dir) {
        return list(dir.toPath());
    }

    public static Stream<File> list(String dir) {
        return list(new File(format(dir)));
    }

    public static Stream<String> lines() {
        BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(System.in, BUFFER_SIZE), UTF_8.newDecoder().onMalformedInput(IGNORE)));
        return br.lines().onClose(asUncheckedRunnable(br));
    }

    public static Stream<String> lines(Path path) {
        try {
            String p = path.toString();
            if (!p.contains("*")) {
                return p.endsWith(".gz") ? GZIPFiles.lines(path)
                        : p.endsWith(".bz2") ? BZIP2Files.lines(path)
                        : overridenLines(path);
            } else {
                File file = path.toFile();
                return Stream.of(file.getParentFile()
                        .listFiles((dir, name) -> name.matches(file.getName().replace(".", "\\.").replace("*", ".+"))))
                        .sorted()
                        .flatMap(AllFiles::lines);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Stream<String> overridenLines(Path path) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new BufferedInputStream(newInputStream(path), BUFFER_SIZE), UTF_8.newDecoder().onMalformedInput(IGNORE)));
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

    public static Stream<String> lines(File file) {
        return lines(file.toPath());
    }

    public static Stream<String> lines(String path) {
        return lines(new File(format(path)));
    }

    public static Path write(Path path, Stream<?> lines, Charset cs, OpenOption... options) {
        if (options.length == 0) {
            options = new OpenOption[]{CREATE_NEW};
        }
        Objects.requireNonNull(lines);
        CharsetEncoder encoder = cs.newEncoder();
        try {
            OutputStream out = newOutputStream(path, options);
            if (path.toString().endsWith(".gz")) {
                out = new GZIPOutputStream(out);
            } else if (path.toString().endsWith(".bz2")) {
                out = new BZip2CompressorOutputStream(out);
            }
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, encoder), BUFFER_SIZE)) {
                lines.forEach(line -> {
                    try {
                        writer.append(line.toString());
                        writer.newLine();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return path;
    }

    public static Path write(Path path, Stream<?> lines, OpenOption... options) {
        return write(path, lines, UTF_8, options);
    }

    public static Path write(String path, Iterable<? extends CharSequence> lines, OpenOption... options) {
        return write(new File(format(path)), lines, options);
    }

    public static Path write(String path, Iterable<? extends CharSequence> lines, boolean removeOldFile, OpenOption... options) {
        path = format(path);
        if (removeOldFile) {
            new File(path).delete();
        }
        return write(new File(path), lines, options);
    }

    public static Path write(String path, Stream<?> lines, OpenOption... options) {
        return write(new File(format(path)), lines, options);
    }

    public static Path write(String path, Stream<?> lines, boolean removeOldFile, OpenOption... options) {
        path = format(path);
        if (removeOldFile) {
            new File(path).delete();
        }
        return write(new File(path), lines, options);
    }

    public static void write(Stream<?> lines) {
        lines.forEach(System.out::println);
    }

    public static void write(Iterable<?> lines) {
        lines.forEach(System.out::println);
    }

    public static Path write(File file, Iterable<? extends CharSequence> lines, OpenOption... options) {
        return write(file.toPath(), lines, options);
    }

    public static Path write(File file, Stream<?> lines, OpenOption... options) {
        return write(file.toPath(), lines, options);
    }

    public static Path write(Path path, Iterable<? extends CharSequence> lines, OpenOption... options) {
        if (options.length == 0) {
            options = new OpenOption[]{CREATE_NEW};
        }
        try {
            File parent = path.getParent().toFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            return path.toString().endsWith(".gz") ? GZIPFiles.write(path, lines, UTF_8, options)
                    : path.toString().endsWith(".bz2") ? BZIP2Files.write(path, lines, UTF_8, options)
                    : Files.write(path, lines, UTF_8, options);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void writeln(String path, String line) {
        try {
            OutputStream os = new FileOutputStream(format(path), true);
            if (path.endsWith(".gz")) {
                os = new GZIPOutputStream(os);
            } else if (path.endsWith(".bz2")) {
                os = new BZip2CompressorOutputStream(os);
            }
            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"))) {
                out.write(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
