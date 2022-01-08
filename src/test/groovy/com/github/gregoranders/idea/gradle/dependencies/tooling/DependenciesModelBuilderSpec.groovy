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
package com.github.gregoranders.idea.gradle.dependencies.tooling

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencySet
import spock.lang.*

import java.util.stream.Stream

@Title('Dependencies model builder')
@Narrative('''
> # As a user I would like to be able to get a custom [model][model-url] from a [Gradle][gradle-url] build to collect dependencies.

[gradle-url]: https://gradle.org
[model-url]: https://github.com/bmuschko/tooling-api-custom-model
''')
@Subject(DependenciesModelBuilder)
@See([
    'https://gradle.org',
    'https://github.com/bmuschko/tooling-api-custom-model',
    'https://docs.gradle.org/current/javadoc/org/gradle/tooling/provider/model/ToolingModelBuilder.html',
    'https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/com/github/gregoranders/idea/gradle/dependencies/tooling/DependenciesModelBuilder.java'
])
@Issue([
    '5'
])
class DependenciesModelBuilderSpec extends Specification {

    @Subject
    DependenciesModelBuilder testSubject = new DependenciesModelBuilder()

    @Unroll
    def "should return #expectedResult when invoked with #modelName as the modelName"() {
        expect: 'should return appropriate result when invoked'
            testSubject.canBuild(modelName) == expectedResult
        where:
            modelName                                                                          || expectedResult
            'com.github.gregoranders.idea.gradle.dependencies.tooling.model.api.Project'       || true
            'com.github.gregoranders.idea.gradle.dependencies.tooling.model.api.Configuration' || false
            'com.github.gregoranders.idea.gradle.dependencies.tooling.model.api.Dependency'    || false
    }

    def 'should return project model'() {
        given: 'a mocked Project'
            Project mockedProject = Mock()
        and: 'a mocked ConfigurationContainer'
            ConfigurationContainer mockedConfigurationsContainer = Mock()
        and: 'a mocked Configuration'
            Configuration mockedConfiguration = Mock()
        and: 'a mocked DependencySet'
            DependencySet mockedDependencySet = Mock()
        and: 'a mocked Dependency'
            Dependency mockedDependency = Mock()
        when: 'buildAll is invoked on the unit under test with the mocked project as a parameter'
            def project = (testSubject.buildAll('test', mockedProject) as com.github.gregoranders.idea.gradle.dependencies.tooling.model.api.Project)
        then: 'all assumptions should be satisfied'
            interaction {
                1 * mockedDependency.getGroup() >> 'testDependencyGroup'
                1 * mockedDependency.getName() >> 'testDependencyName'
                1 * mockedDependency.getVersion() >> 'testDependencyVersion'
                1 * mockedDependencySet.stream() >> Stream.of(mockedDependency)
                1 * mockedConfiguration.getDependencies() >> mockedDependencySet
                1 * mockedConfiguration.getName() >> 'testConfigurationName'
                1 * mockedConfigurationsContainer.stream() >> Stream.of(mockedConfiguration)
                1 * mockedProject.getConfigurations() >> mockedConfigurationsContainer
                1 * mockedProject.getName() >> 'testProjectName'
                1 * mockedProject.getDescription() >> 'testProjectDescription'
                1 * mockedProject.getVersion() >> 'testProjectVersion'
                1 * mockedProject.getPath() >> 'testProjectPath'
            }
        and: 'a project model should be returned'
            project instanceof com.github.gregoranders.idea.gradle.dependencies.tooling.model.api.Project
        and: 'it should have the name set to "testProjectName"'
            project.name() == 'testProjectName'
        and: 'it should have the description set to "testProjectDescription"'
            project.description() == 'testProjectDescription'
        and: 'it should have the version set to "testProjectVersion"'
            project.version() == 'testProjectVersion'
        and: 'it should have the path set to "testProjectPath"'
            project.path() == 'testProjectPath'
        and: 'it should contain one configuration'
            project.configurations().size() == 1
        and: 'this configuration should have the name "testConfigurationName"'
            project.configurations().get(0).name() == 'testConfigurationName'
        and: 'it should contain one dependency'
            project.configurations().get(0).dependencies().size() == 1
        and: 'this dependency should have the name of "testDependencyName"'
            project.configurations().get(0).dependencies().get(0).name() == 'testDependencyName'
        and: 'it should have the group of "testDependencyGroup"'
            project.configurations().get(0).dependencies().get(0).group() == 'testDependencyGroup'
        and: 'it should have the version of "testDependencyVersion"'
            project.configurations().get(0).dependencies().get(0).version() == 'testDependencyVersion'
        and: 'no exceptions should be thrown'
            noExceptionThrown()
    }
}
