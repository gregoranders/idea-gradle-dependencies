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
package io.github.gregoranders.idea.gradle.dependencies.model


import io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Configuration
import io.github.gregoranders.idea.gradle.dependencies.model.api.Mapper
import io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Dependency
import io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.ImmutableConfiguration
import io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.ImmutableDependency
import io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.ImmutableProject
import io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Project
import spock.lang.*

@Title('Default mapper')
@Narrative('''
> # As a user I would like to be able to map the gradle model into the application model to be able to differentiate between subprojects as dependencies an 'external' dependencies.

''')
@Subject([DefaultMapper, Mapper])
@See([
    'https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/io/github/gregoranders/idea/gradle/dependencies/model/Mapper.java',
    'https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/io/github/gregoranders/idea/gradle/dependencies/model/DefaultMapper.java'
])
@Issue([
    '28'
])
class DefaultMapperSpec extends Specification {

    @Subject
    Mapper testSubject = new DefaultMapper()

    def 'should returned mapped model'() {
        given: 'a project with a subproject as a dependency'
            Dependency subDependency = ImmutableDependency.of('subDependencyGroup', 'subDependencyName', 'subDependencyVersion')
            Configuration subConfiguration = ImmutableConfiguration.of('subConfigurationName', Set.of(subDependency))
            Project subProject = ImmutableProject.of('subProjectGroup', 'subProjectName', 'subProjectDescription', 'subProjectVersion', 'subProjectPath', Set.of(subConfiguration), Set.of())
            Dependency dependencySub = ImmutableDependency.of('subProjectGroup', 'subProjectName', 'subProjectVersion')
            Dependency dependencyOne = ImmutableDependency.of('subProjectGroup', 'subProjectName', 'dependencyVersion')
            Dependency dependencyTwo = ImmutableDependency.of('subProjectGroup', 'dependencyName', 'dependencyVersion')
            Dependency dependencyThree = ImmutableDependency.of('dependencyGroup', 'dependencyName', 'dependencyVersion')
            Configuration configuration = ImmutableConfiguration.of('configurationName', new LinkedHashSet([dependencySub, dependencyOne, dependencyTwo, dependencyThree]))
            Project project = ImmutableProject.of('projectGroup', 'projectName', null, 'projectVersion', 'projectPath', Set.of(configuration), Set.of(subProject))
        when: 'the unit under test maps this project'
            def mappedProject = testSubject.map(project)
        then: 'the project should match the expectations'
            mappedProject.group() == 'projectGroup'
            mappedProject.name() == 'projectName'
            mappedProject.description() == ''
            mappedProject.version() == 'projectVersion'
            mappedProject.path() == 'projectPath'
            mappedProject.subProjects().size() == 1
            mappedProject.configurations().size() == 1
            mappedProject.configurations()[0].name() == 'configurationName'
        and: 'the mapped subProject dependency should be marked as a subProject dependency and match the expected values'
            mappedProject.configurations().forEach(config -> {
                assert config.dependencies()[0].group() == 'subProjectGroup'
                assert config.dependencies()[0].name() == 'subProjectName'
                assert config.dependencies()[0].version() == 'subProjectVersion'
                assert config.dependencies()[0].isSubProject()
            })
        and: 'the other dependencies should not'
            mappedProject.configurations().forEach(config -> {
                assert !config.dependencies()[1].isSubProject()
                assert !config.dependencies()[2].isSubProject()
                assert !config.dependencies()[3].isSubProject()
            })
        and: 'no exceptions should be thrown'
            noExceptionThrown()
    }
}
