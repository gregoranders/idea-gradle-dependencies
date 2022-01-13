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
package io.github.gregoranders.idea.gradle.dependencies.model;


import io.github.gregoranders.idea.gradle.dependencies.model.api.Configuration;
import io.github.gregoranders.idea.gradle.dependencies.model.api.Dependency;
import io.github.gregoranders.idea.gradle.dependencies.model.api.ImmutableConfiguration;
import io.github.gregoranders.idea.gradle.dependencies.model.api.ImmutableDependency;
import io.github.gregoranders.idea.gradle.dependencies.model.api.ImmutableProject;
import io.github.gregoranders.idea.gradle.dependencies.model.api.Mapper;
import io.github.gregoranders.idea.gradle.dependencies.model.api.Project;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("PMD.LawOfDemeter")
public final class DefaultMapper implements Mapper {

    private final Set<io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Project> subProjects = new HashSet<>();

    @Override
    public Project map(final io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Project project) {
        subProjects.add(project);
        final Set<Project> projects = mapSubProjects(project.subProjects());
        return ImmutableProject.of(project.group(), project.name(), mapNullable(project.description()), project.version(), project.path(),
            mapConfigurations(project.configurations()), projects);
    }

    private Set<Configuration> mapConfigurations(
        final Set<io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Configuration> configurations) {
        return configurations
            .stream()
            .map(this::mapConfiguration)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Configuration mapConfiguration(final io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Configuration configuration) {
        return ImmutableConfiguration.of(configuration.name(), mapDependencies(configuration.dependencies()));
    }

    private Set<Dependency> mapDependencies(final Set<io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Dependency> dependencies) {
        return dependencies
            .stream()
            .map(this::mapDependency)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Dependency mapDependency(final io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Dependency dependency) {
        return ImmutableDependency.of(mapNullable(dependency.group()), dependency.name(), mapNullable(dependency.version()), isSubProject(dependency));
    }

    private String mapNullable(@Nullable final String nullableText) {
        return nullableText == null ? "" : nullableText;
    }

    private boolean isSubProject(final io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Dependency dependency) {
        return subProjects
            .stream()
            .anyMatch(
                project ->
                    project.group().equals(dependency.group())
                        &&
                        project.name().equals(dependency.name())
                        &&
                        project.version().equals(dependency.version())
            );
    }

    private Set<Project> mapSubProjects(final Set<io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Project> projects) {
        return projects
            .stream()
            .map(this::map)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
