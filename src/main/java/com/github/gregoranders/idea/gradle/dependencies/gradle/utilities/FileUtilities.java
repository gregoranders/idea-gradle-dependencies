/*
 * MIT License
 *
 * Copyright (c) 2022 Gregor Anders
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.gregoranders.idea.gradle.dependencies.gradle.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public final class FileUtilities {

    private FileUtilities() {
    }

    public static String removeLeadingFile(final String path) {
        return path.startsWith("file:") ? path.substring(5) : path;
    }

    public static String removeLeadingJar(final String path) {
        return path.startsWith("jar:") ? path.substring(4, path.length() - 2) : path;
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    public static String getResourceAsString(final Class<?> clazz, final String scriptPath) throws IOException {
        try (InputStream resource = getResourceAsStream(clazz, scriptPath)) {
            return inputStreamToString(resource);
        }
    }

    public static String inputStreamToString(final InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    public static InputStream getResourceAsStream(final Class<?> clazz, final String path) {
        return clazz.getResourceAsStream(path);
    }

    public static String getAbsolutePathAsString(final Path path) {
        return getPathAsString(getAbsolutePath(path));
    }

    public static String getPathAsString(final Path path) {
        return path.toString();
    }

    public static Path getAbsolutePath(final Path path) {
        return path.toAbsolutePath();
    }

}
