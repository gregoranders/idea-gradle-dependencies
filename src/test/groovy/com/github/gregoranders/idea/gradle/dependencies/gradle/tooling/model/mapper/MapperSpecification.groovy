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
package com.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.mapper

import com.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Dependency
import com.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Project
import spock.lang.Specification

class MapperSpecification extends Specification {

    void verifyProject(Project project, String group, String name, String description, String version, String path) {
        verifyAll(project) {
            it.group() == group
            it.name() == name
            it.description() == description
            it.version() == version
            it.path() == path
        }
    }

    void verifyDependency(Dependency dependency, String group, String name, String version) {
        verifyAll(dependency) {
            it.group() == group
            it.name() == name
            it.version() == version
        }
    }
}
