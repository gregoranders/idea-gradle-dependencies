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
package com.github.gregoranders.idea.gradle.dependencies.gradle;

import com.github.gregoranders.idea.gradle.dependencies.gradle.configuration.Configuration;
import com.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Project;
import com.github.gregoranders.idea.gradle.dependencies.gradle.utilities.InitScriptInjector;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;

import java.nio.file.Path;

public final class GradleUtilities {

    private final Configuration configuration;

    public GradleUtilities(final Configuration config) {
        configuration = config;
    }

    @SuppressWarnings({"UnstableApiUsage", "PMD.AvoidCatchingGenericException", "PMD.AvoidUncheckedExceptionsInSignatures", "PMD.LawOfDemeter"})
    public Project getDependencies(final Path path) throws GradleUtilitiesException {
        try {
            final GradleConnector connector = getGradleConnector(GradleConnector.newConnector(), path);

            try (ProjectConnection projectConnection = getProjectConnection(connector)) {

                final ModelBuilder<Project> modelBuilder = projectConnection.model(Project.class);

                try (InitScriptInjector initScriptInjector = new InitScriptInjector(configuration.getInitScriptPath(), configuration.getPluginName())) {
                    modelBuilder.withArguments("--init-script", initScriptInjector.getAbsolutePath());
                    return modelBuilder.get();
                }
            } finally {
                connector.disconnect();
            }
        } catch (Exception exception) {
            throw new GradleUtilitiesException(exception);
        }
    }

    private ProjectConnection getProjectConnection(final GradleConnector connector) {
        return connector.connect();
    }

    private GradleConnector getGradleConnector(final GradleConnector connector, final Path path) {
        return connector.forProjectDirectory(path.toFile());
    }
}
