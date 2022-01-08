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
package com.github.gregoranders.idea.gradle.dependencies

import spock.lang.*

import java.nio.file.Path

@Title('Gradle utilities')
@Narrative('''
> # As a user I would like to be able to collect dependencies of a [Gradle][url] JVM project.

[url]: https://gradle.org
''')
@Subject(GradleUtilities)
@See([
    'https://gradle.org',
    'https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/com/github/gregoranders/idea/gradle/dependencies/GradleUtilities.java'
])
@Issue([
    '2', '6'
])
class GradleUtilitiesSpec extends Specification {

    @Subject
    GradleUtilities testSubject = new GradleUtilities()

    def 'should return an empty list of dependencies of a simple project with no dependencies'() {
        given: 'a path to a simple project with no dependencies'
            def path = getProjectPath('simple-no-dependencies')
        when: 'the test subject invokes getDependencies with this path'
            def dependencies = testSubject.getDependencies(path)
        then: 'a list with zero dependencies should be returned'
            dependencies.size() == 0
        and: 'no exceptions are thrown'
            noExceptionThrown()
    }

    def getProjectPath(String project) {
        Path.of(GradleUtilitiesSpec.getResource("/projects/${project}").toURI())
    }
}