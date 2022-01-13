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
package io.github.gregoranders.idea.gradle.dependencies.gradle.tooling;

import io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.mapper.ProjectMapper;
import org.gradle.api.NonNullApi;
import org.gradle.api.Project;
import org.gradle.tooling.provider.model.ToolingModelBuilder;

@NonNullApi
public final class DependenciesModelBuilder implements ToolingModelBuilder {

    private final ProjectMapper projectMapper;

    public DependenciesModelBuilder(final ProjectMapper mapper) {
        projectMapper = mapper;
    }

    @Override
    public boolean canBuild(final String modelName) {
        return modelName.equals(io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Project.class.getName());
    }

    @Override
    public Object buildAll(final String modelName, final Project project) {
        return projectMapper.map(project);
    }
}
