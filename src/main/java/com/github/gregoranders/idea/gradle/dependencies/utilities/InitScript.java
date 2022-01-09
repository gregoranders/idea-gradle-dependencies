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
package com.github.gregoranders.idea.gradle.dependencies.utilities;

import org.gradle.internal.impldep.org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.util.List;
import java.util.Objects;

public final class InitScript implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(InitScript.class);

    private final String initScriptPath;

    private Path temporaryScriptPath;

    public InitScript(final String scriptPath) {
        initScriptPath = scriptPath;
    }

    @Override
    public void close() throws Exception {
        if (temporaryScriptPath != null) {
            Files.delete(temporaryScriptPath);
        }
    }

    public String getAbsolutePath() throws URISyntaxException, IOException {
        final Path path = getTemporaryInitScriptPath();
        return FilenameUtils.separatorsToUnix(getAbsolutePathAsString(path));
    }

    private Path getTemporaryInitScriptPath() throws URISyntaxException, IOException {
        return createTemporaryInitScript(getPluginPath(), initScriptPath);
    }

    private Path createTemporaryInitScript(final Path pluginPath, final String path) throws IOException, URISyntaxException {
        final Path scriptPath = getInitScriptPath(path);
        temporaryScriptPath = createSecureTempFile();

        final List<String> lines = Files.readAllLines(scriptPath, StandardCharsets.UTF_8);
        final String initScriptContent = getInitScriptContentWithReplacedPluginPath(pluginPath, lines);

        return Files.writeString(temporaryScriptPath, initScriptContent, StandardCharsets.UTF_8);
    }

    @SuppressWarnings({"PMD.LawOfDemeter", "java:S5443"})
    private Path createSecureTempFile() throws IOException {
        final Path path = Files.createTempFile("gradle-dependencies-plugin-init", ".gradle");
        setFilePermissions(path.toFile());
        return path;
    }

    @SuppressWarnings({"java:S899", "ResultOfMethodCallIgnored"})
    private void setFilePermissions(final File file) {
        file.setReadable(true, true);
        file.setWritable(true, true);
        file.setExecutable(false, true);
    }

    private String getInitScriptContentWithReplacedPluginPath(final Path pluginPath, final List<String> lines) {
        final StringBuilder stringBuilder = new StringBuilder();
        lines.forEach(line -> {
            stringBuilder.append(line.replace("GRADLE_DEPENDENCIES_PLUGIN_PATH", getAbsolutePathAsString(pluginPath)));
            stringBuilder.append(System.lineSeparator());
        });

        LOG.error("Content: {}", stringBuilder);

        return stringBuilder.toString();
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private Path getPluginPath() throws URISyntaxException {
        final CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
        return Path.of(codeSource.getLocation().toURI());
    }

    private Path getInitScriptPath(final String scriptPath) throws URISyntaxException {
        final URL resource = getResource(getClass(), scriptPath);
        Objects.requireNonNull(resource, String.format("Resource not found %s", scriptPath));
        return getPathFromURL(resource);
    }

    private URL getResource(final Class<?> clazz, final String path) {
        return clazz.getResource(path);
    }

    private Path getPathFromURL(final URL url) throws URISyntaxException {
        return Path.of(url.toURI());
    }

    private String getAbsolutePathAsString(final Path path) {
        return getPathAsString(getAbsolutePath(path));
    }

    private String getPathAsString(final Path path) {
        return path.toString();
    }

    private Path getAbsolutePath(final Path path) {
        return path.toAbsolutePath();
    }
}
