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

import com.github.gregoranders.idea.gradle.dependencies.configuration.Configuration
import io.github.joke.spockmockable.Mockable
import spock.lang.*

import java.nio.file.Files
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
    '2', '5', '6'
])
@Mockable(Configuration)
class GradleUtilitiesSpec extends Specification {

    final Configuration configuration = new Configuration()

    @Subject
    GradleUtilities testSubject = new GradleUtilities(configuration)

    def 'should return temporary init script path with replaced plugin path'() {
        when: 'a temporary init script is requested'
            def path = testSubject.getTemporaryInitScriptPath()
        then: 'the script exists'
            Files.exists(path)
        and: 'it contains the replaced path to the plugin'
            def lines = Files.readAllLines(path)
            checkPluginPath(lines)
        and: 'no exceptions are thrown'
            noExceptionThrown()
    }

    def 'should throw exception when not existent init script is provided'() {
        given: 'a configuration mock to return an invalid init script path'
            Configuration configuration = Mock()
        and: 'a test subject with this configuration'
            GradleUtilities testSubject = new GradleUtilities(configuration)
        when: 'a temporary init scrip is requested'
            testSubject.getTemporaryInitScriptPath()
        then: 'the provided configuration mock should return an invalid script path'
            interaction {
                1 * configuration.getInitScriptPath() >> '/test'
            }
        and: 'an exception should be thrown'
            NullPointerException exception = thrown()
        and: 'the message of the exception should be "Resource not found /test"'
            exception.getMessage() == 'Resource not found /test'
    }

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

    def checkPluginPath(List<String> lines) {
        boolean found = false
        lines.forEach(line -> {
            if (line.contains("/build/classes/java/main')")) {
                found = true
            }
        })
        found
    }
}
