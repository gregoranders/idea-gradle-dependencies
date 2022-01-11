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
import org.gradle.api.artifacts.DependencySet
import spock.lang.Issue
import spock.lang.Narrative
import spock.lang.See
import spock.lang.Subject

import java.util.stream.Stream

@Narrative('''
> # As a user I would like to map a [Gradle][gradle-url] [DependencySet][dependencySet-url] to a set of [model][model-url] using a custom [mapper][mapper-url].

[gradle-url]: https://gradle.org
[dependencySet-url]: https://docs.gradle.org/current/javadoc/org/gradle/api/artifacts/DependencySet.html
[model-url]: https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/com/github/gregoranders/idea/gradle/dependencies/gradle/tooling/model/api/Dependency.java
[mapper-url]: https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/com/github/gregoranders/idea/gradle/dependencies/gradle/tooling/model/mapper/DependencySetMapper.java
''')
@Subject([DependencySetMapper, Dependency])
@See([
    'https://docs.gradle.org/current/javadoc/org/gradle/api/artifacts/Dependency.html',
    'https://docs.gradle.org/current/javadoc/org/gradle/api/artifacts/DependencySet.html',
    'https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/com/github/gregoranders/idea/gradle/dependencies/gradle/tooling/model/api/Dependency.java',
    'https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/com/github/gregoranders/idea/gradle/dependencies/gradle/tooling/model/mapper/DependencySetMapper.java',
])
@Issue([
    '40'
])
class DependencySetMapperSpec extends MapperSpecification {

    def dependencyMapper = new DependencyMapper()

    @Subject
    def testSubject = new DependencySetMapper(dependencyMapper)

    def 'should map a gradle dependency set preserving the order'() {
        given: 'a gradle dependency A'
            def dependencyA = Mock(org.gradle.api.artifacts.Dependency)
        and: 'a gradle dependency B'
            def dependencyB = Mock(org.gradle.api.artifacts.Dependency)
        and: 'a gradle dependency set containing A and B'
            def dependencySet = Mock(DependencySet)
        when: 'the unit under test maps this set'
            def set = testSubject.map(dependencySet)
        then: 'following interactions should be executed'
            interaction {
                1 * dependencySet.stream() >> Stream.of(dependencyA, dependencyB)

                1 * dependencyA.getGroup() >> 'dependencyAGroup'
                1 * dependencyA.getName() >> 'dependencyAName'
                1 * dependencyA.getVersion() >> 'dependencyAVersion'

                1 * dependencyB.getGroup() >> 'dependencyBGroup'
                1 * dependencyB.getName() >> 'dependencyBName'
                1 * dependencyB.getVersion() >> 'dependencyBVersion'
            }
        and: 'the returned set should contain two mapped dependencies'
            set.size() == 2
        and: 'the first dependency should be of the expected type'
            set[0] instanceof Dependency
        and: 'have the expected values'
            verifyDependency(set[0], 'dependencyAGroup', 'dependencyAName', 'dependencyAVersion')
        and: 'the second dependency should be of the expected type'
            set[1] instanceof Dependency
        and: 'have the expected values'
            verifyDependency(set[1], 'dependencyBGroup', 'dependencyBName', 'dependencyBVersion')
        and: 'no exceptions should be thrown'
            noExceptionThrown()
    }
}
