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


import com.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import spock.lang.*

import java.util.stream.Stream

@Narrative('''
> # As a user I would like to map a [Gradle][gradle-url] [project][project-url] to a [model][model-url] using a custom [mapper][mapper-url].

[gradle-url]: https://gradle.org
[project-url]: https://docs.gradle.org/current/javadoc/org/gradle/api/Project.html
[model-url]: https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/com/github/gregoranders/idea/gradle/dependencies/gradle/tooling/model/api/Project.java
[mapper-url]: https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/com/github/gregoranders/idea/gradle/dependencies/gradle/tooling/model/mapper/ProjectMapper.java
''')
@Subject([ProjectMapper, Project])
@See([
    'https://docs.gradle.org/current/javadoc/org/gradle/api/Project.html',
    'https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/com/github/gregoranders/idea/gradle/dependencies/gradle/tooling/model/api/Project.java',
    'https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/com/github/gregoranders/idea/gradle/dependencies/gradle/tooling/model/mapper/ProjectMapper.java',
])
@Issue([
    '40'
])
class ProjectMapperSpec extends MapperSpecification {

    def dependencyMapper = new DependencyMapper()

    def dependencySetMapper = new DependencySetMapper(dependencyMapper)

    def configurationMapper = new ConfigurationMapper(dependencySetMapper)

    def configurationContainerMapper = new ConfigurationContainerMapper(configurationMapper)

    @Subject
    def testSubject = new ProjectMapper(configurationContainerMapper)

    @Unroll("should map a gradle project of { group: '#group' name: '#name' description: #description version: '#version' path: #path } to { group: '#expectedGroup' name: '#expectedName' description: '#expectedDescription' version: '#expectedVersion' path '#expectedPath'}")
    def 'should map a gradle project'() {
        given: 'a mocked gradle project'
            def mock = Mock(org.gradle.api.Project)
        when: 'the unit under test maps this project'
            def model = testSubject.map(mock)
        then: 'following interactions should be executed'
            interaction {
                1 * mock.getGroup() >> group
                1 * mock.getName() >> name
                1 * mock.getDescription() >> description
                1 * mock.getVersion() >> version
                1 * mock.getPath() >> path
                1 * mock.getConfigurations() >> {
                    def mockedContainerConfiguration = Mock(ConfigurationContainer)
                    1 * mockedContainerConfiguration.stream() >> Stream.empty()
                    mockedContainerConfiguration
                }
                1 * mock.getSubprojects() >> Set.of()
            }
        and: 'the returned model should be of the expected type'
            model instanceof Project
        and: 'have the expected values'
            verifyProject(model, expectedGroup, expectedName, expectedDescription, expectedVersion, expectedPath)
        and: 'no exceptions should be thrown'
            noExceptionThrown()
        where:
            group          | name          | description          | version          | path          || expectedGroup  | expectedName  | expectedDescription  | expectedVersion  | expectedPath
            'projectGroup' | 'projectName' | 'projectDescription' | 'projectVersion' | 'projectPath' || 'projectGroup' | 'projectName' | 'projectDescription' | 'projectVersion' | 'projectPath'
            'projectGroup' | 'projectName' | null                 | 'projectVersion' | 'projectPath' || 'projectGroup' | 'projectName' | ''                   | 'projectVersion' | 'projectPath'
    }
}
