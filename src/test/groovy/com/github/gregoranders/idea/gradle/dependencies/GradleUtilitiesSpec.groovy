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
import com.github.gregoranders.idea.gradle.dependencies.tooling.model.api.Dependency
import io.github.joke.spockmockable.Mockable
import org.gradle.tooling.BuildException
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
    '2', '5', '6', '14', '17', '22', '24'
])
@Mockable(Configuration)
class GradleUtilitiesSpec extends Specification {

    final Configuration configuration = new Configuration()

    @Subject
    GradleUtilities testSubject = new GradleUtilities(configuration)

    def 'should return an empty set of dependencies of a simple project with no dependencies'() {
        given: 'a path to a simple project with no dependencies'
            def path = getProjectPath('simple-no-dependencies')
        when: 'the test subject invokes getDependencies with this path'
            def project = testSubject.getDependencies(path)
        then: 'the group of the project should be "com.github.gregoranders"'
            project.group() == 'com.github.gregoranders'
        and: 'the name of the project should be "simple-no-dependencies"'
            project.name() == 'simple-no-dependencies'
        and: 'the version of the project should be "0.0.1"'
            project.version() == '0.0.1'
        and: 'the description of the project should be "Simple project no dependencies"'
            project.description() == 'Simple project no dependencies'
        and: 'the project should have no dependencies in all configurations'
            project.configurations().forEach(configuration -> {
                assert configuration.dependencies().size() == 0
            })
        and: 'no exceptions are thrown'
            noExceptionThrown()
    }

    def 'should return a set of dependencies of a simple project with dependencies'() {
        given: 'a path to a simple project with no dependencies'
            def path = getProjectPath('simple-with-dependencies')
        when: 'the test subject invokes getDependencies with this path'
            def project = testSubject.getDependencies(path)
        then: 'the group of the project should be "com.github.gregoranders"'
            project.group() == 'com.github.gregoranders'
        and: 'the name of the project should be "simple-no-dependencies"'
            project.name() == 'simple-with-dependencies'
        and: 'the version of the project should be "0.0.2"'
            project.version() == '0.0.2'
        and: 'the description of the project should be "Simple project no dependencies"'
            project.description() == 'Simple project with dependencies'
        and: 'the project should have 1 dependency in the implementation configurations'
            project.configurations().forEach(configuration -> {
                if (configuration.name() == 'implementation') {
                    assert configuration.dependencies().size() == 1
                    assertDependency(configuration.dependencies()[0], 'org.slf4j', 'slf4j-api', '1.7.32')
                }
            })
        and: 'the project should have 2 dependencies in the runtimeOnly configurations'
            project.configurations().forEach(configuration -> {
                if (configuration.name() == 'runtimeOnly') {
                    assert configuration.dependencies().size() == 2
                    assertDependency(configuration.dependencies()[0], 'ch.qos.logback', 'logback-core', '1.2.9')
                    assertDependency(configuration.dependencies()[1], 'ch.qos.logback', 'logback-classic', '1.2.9')
                }
            })
        and: 'no exceptions are thrown'
            noExceptionThrown()
    }

    def 'should return a multi project with no dependencies'() {
        given: 'a path to a multi project with no dependencies'
            def path = getProjectPath('multi-no-dependencies')
        when: 'the test subject invokes getDependencies with this path'
            def project = testSubject.getDependencies(path)
        then: 'the group of the project should be "com.github.gregoranders"'
            project.group() == 'com.github.gregoranders'
        and: 'the name of the project should be "multi-no-dependencies"'
            project.name() == 'multi-no-dependencies'
        and: 'the version of the project should be "0.0.1"'
            project.version() == '0.0.1'
        and: 'the description of the project should be "Multi project no dependencies"'
            project.description() == 'Multi project no dependencies'
        and: 'the project should have no dependencies in all configurations'
            project.configurations().forEach(configuration -> {
                assert configuration.dependencies().size() == 0
            })
        and: 'the project should have 3 sub projects'
            project.subProjects().size() == 3
        and: 'the first sub project should be named "api"'
            project.subProjects()[0].name() == 'api'
        and: 'the group of it should be "com.github.gregoranders"'
            project.subProjects()[0].group() == 'com.github.gregoranders'
        and: 'the second sub project should be named "impl"'
            project.subProjects()[1].name() == 'impl'
        and: 'the group of it should be "com.github.gregoranders"'
            project.subProjects()[1].group() == 'com.github.gregoranders'
        and: 'the third sub project should be named "test"'
            project.subProjects()[2].name() == 'test'
        and: 'the group of it should be "com.github.gregoranders"'
            project.subProjects()[2].group() == 'com.github.gregoranders'
        and: 'no exceptions are thrown'
            noExceptionThrown()
    }

    def 'should return a multi project with dependencies'() {
        given: 'a path to a multi project with dependencies'
            def path = getProjectPath('multi-with-dependencies')
        when: 'the test subject invokes getDependencies with this path'
            def project = testSubject.getDependencies(path)
        then: 'the name of the project should be "multi-with-dependencies"'
            project.name() == 'multi-with-dependencies'
        and: 'the version of the project should be "0.0.1"'
            project.version() == '0.0.1'
        and: 'the description of the project should be "Multi project with dependencies"'
            project.description() == 'Multi project with dependencies'
        and: 'the project should have no dependencies in all configurations'
            project.configurations().forEach(configuration -> {
                assert configuration.dependencies().size() == 0
            })
        and: 'the project should have 3 sub projects'
            project.subProjects().size() == 3
        and: 'the first sub project should be named "api"'
            project.subProjects()[0].name() == 'api'
        and: 'it should have the version "0.0.2"'
            project.subProjects()[0].version() == '0.0.2'
        and: 'it should have no dependencies in all configurations'
            project.subProjects()[0].configurations().forEach(configuration -> {
                assert configuration.dependencies().size() == 0
            })
        and: 'the second sub project should be named "impl"'
            project.subProjects()[1].name() == 'impl'
        and: 'it should have the version "0.0.2"'
            project.subProjects()[1].version() == '0.0.3'
        and: 'it should have 2 dependencies in the implementation configuration'
            project.subProjects()[1].configurations().forEach(configuration -> {
                if (configuration.name() == 'implementation') {
                    assert configuration.dependencies().size() == 2

                    assertDependency(configuration.dependencies()[0], 'com.github.gregoranders', 'api', '0.0.2')
                    assertDependency(configuration.dependencies()[1], 'org.slf4j', 'slf4j-api', '1.7.32')
                }
            })
        and: 'the third sub project should be named "test"'
            project.subProjects()[2].name() == 'test'
        and: 'it should have the version "0.0.4"'
            project.subProjects()[2].version() == '0.0.4'
        and: 'it should have 2 dependencies in the implementation configuration'
            project.subProjects()[2].configurations().forEach(configuration -> {
                if (configuration.name() == 'implementation') {
                    assert configuration.dependencies().size() == 2

                    assertDependency(configuration.dependencies()[0], 'com.github.gregoranders', 'impl', '0.0.3')
                    assertDependency(configuration.dependencies()[1], 'org.slf4j', 'slf4j-api', '1.7.32')
                }
            })
        and: 'it should have 1 dependencies in the testImplementation configuration'
            project.subProjects()[2].configurations().forEach(configuration -> {
                if (configuration.name() == 'testImplementation') {
                    assert configuration.dependencies().size() == 1

                    assertDependency(configuration.dependencies()[0], 'org.junit.jupiter', 'junit-jupiter-engine', '5.8.2')
                }
            })
        and: 'no exceptions are thrown'
            noExceptionThrown()
    }

    def 'should throw an exception'() {
        given: 'an invalid path'
            def path = Path.of('foo')
        when: 'the test subject invokes getDependencies with this path'
            testSubject.getDependencies(path)
        then: 'an exception is thrown'
            GradleUtilitiesException exception = thrown()
        and: 'the message of the exception matches'
            exception.getMessage().contains("Could not fetch model of type 'Project' using connection to Gradle distribution")
        and: 'the cause is of type "BuildException"'
            exception.getCause() instanceof BuildException
    }


    def assertDependency(Dependency dependency, String group, String name, String version) {
        assert dependency.group() == group
        assert dependency.name() == name
        assert dependency.version() == version
    }

    def getProjectPath(String project) {
        Path.of(GradleUtilitiesSpec.getResource("/projects/${project}").toURI())
    }
}
