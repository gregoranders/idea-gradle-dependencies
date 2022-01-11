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
package com.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.mapper;

import org.gradle.api.artifacts.DependencySet;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class DependencySetMapper
    extends BasicMapper<DependencySet, Set<com.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Dependency>> {

    private final DependencyMapper dependencyMapper;

    public DependencySetMapper(final DependencyMapper mapper) {
        super();
        dependencyMapper = mapper;
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    @Override
    public Set<com.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Dependency> map(final DependencySet dependencies) {
        return dependencies
            .stream()
            .map(dependencyMapper::map)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
