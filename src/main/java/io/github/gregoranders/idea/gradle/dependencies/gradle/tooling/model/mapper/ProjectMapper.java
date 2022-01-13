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
package io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.mapper;

import io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.ImmutableProject;
import org.gradle.api.Project;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class ProjectMapper extends BasicMapper<Project, io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Project> {

    private final ConfigurationContainerMapper configurationContainerMapper;

    public ProjectMapper(final ConfigurationContainerMapper mapper) {
        super();
        configurationContainerMapper = mapper;
    }

    @Override
    public io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Project map(final Project project) {
        return ImmutableProject.of(
            mapObjectToString(project.getGroup()),
            project.getName(),
            mapNullValueToEmptyString(project.getDescription()),
            mapObjectToString(project.getVersion()),
            project.getPath(),
            configurationContainerMapper.map(project.getConfigurations()),
            mapSubProjects(project.getSubprojects())
        );
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private Set<io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Project> mapSubProjects(final Set<Project> projects) {
        return projects
            .stream()
            .map(this::map)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
