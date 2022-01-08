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
package com.github.gregoranders.idea.gradle.dependencies.tooling;

import com.github.gregoranders.idea.gradle.dependencies.tooling.model.DefaultConfiguration;
import com.github.gregoranders.idea.gradle.dependencies.tooling.model.DefaultDependency;
import com.github.gregoranders.idea.gradle.dependencies.tooling.model.DefaultProject;
import com.github.gregoranders.idea.gradle.dependencies.tooling.model.api.Configuration;
import org.gradle.api.NonNullApi;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.tooling.provider.model.ToolingModelBuilder;

import java.util.List;

@NonNullApi
public final class DependenciesModelBuilder implements ToolingModelBuilder {

    @Override
    public boolean canBuild(final String modelName) {
        return modelName.equals(com.github.gregoranders.idea.gradle.dependencies.tooling.model.api.Project.class.getName());
    }

    @Override
    public Object buildAll(final String modelName, final Project project) {
        final ConfigurationContainer configurations = project.getConfigurations();

        return new DefaultProject(project.getName(), project.getDescription(), getVersion(project.getVersion()),
            project.getPath(), mapConfigurations(configurations));
    }

    private String getVersion(final Object projectVersion) {
        return projectVersion.toString();
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private List<Configuration> mapConfigurations(final ConfigurationContainer configurations) {
        return configurations
            .stream()
            .map(this::mapConfiguration)
            .toList();
    }

    private Configuration mapConfiguration(final org.gradle.api.artifacts.Configuration configuration) {
        return new DefaultConfiguration(configuration.getName(), mapDependencies(configuration.getDependencies()));
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private List<com.github.gregoranders.idea.gradle.dependencies.tooling.model.api.Dependency> mapDependencies(final DependencySet dependencies) {
        return dependencies
            .stream()
            .map(this::mapDependency)
            .toList();
    }

    private com.github.gregoranders.idea.gradle.dependencies.tooling.model.api.Dependency mapDependency(final Dependency dependency) {
        return new DefaultDependency(dependency.getGroup(), dependency.getName(), dependency.getVersion());
    }
}
