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
import spock.lang.*

@Narrative('''
> # As a user I would like to map a [Gradle][gradle-url] [dependency][dependency-url] to a [model][model-url] using a custom [mapper][mapper-url].

[gradle-url]: https://gradle.org
[dependency-url]: https://docs.gradle.org/current/javadoc/org/gradle/api/artifacts/Dependency.html
[model-url]: https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/com/github/gregoranders/idea/gradle/dependencies/gradle/tooling/model/api/Dependency.java
[mapper-url]: https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/com/github/gregoranders/idea/gradle/dependencies/gradle/tooling/model/mapper/DependencyMapper.java
''')
@Subject([DependencyMapper, Dependency])
@See([
    'https://docs.gradle.org/current/javadoc/org/gradle/api/artifacts/Dependency.html',
    'https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/com/github/gregoranders/idea/gradle/dependencies/gradle/tooling/model/api/Dependency.java',
    'https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/com/github/gregoranders/idea/gradle/dependencies/gradle/tooling/model/mapper/DependencyMapper.java',
])
@Issue([
    '40'
])
class DependencyMapperSpec extends MapperSpecification {

    @Subject
    def testSubject = new DependencyMapper()

    @Unroll("should map a gradle dependency of { group: '#group' name: '#name' version: '#version' } to { group: '#expectedGroup' name: '#expectedName' version: '#expectedVersion' }")
    def 'should map a gradle dependency'() {
        given: 'a mocked gradle dependency'
            def mock = Mock(org.gradle.api.artifacts.Dependency)
        when: 'the unit under test maps this dependency'
            def model = testSubject.map(mock)
        then: 'following interactions should be executed'
            interaction {
                1 * mock.getGroup() >> group
                1 * mock.getName() >> name
                1 * mock.getVersion() >> version
            }
        and: 'the returned model should be of the expected type'
            model instanceof Dependency
        and: 'have the expected values'
            verifyDependency(model, expectedGroup, expectedName, expectedVersion)
        and: 'no exceptions should be thrown'
            noExceptionThrown()
        where:
            group             | name             | version             || expectedGroup     | expectedName     | expectedVersion
            'dependencyGroup' | 'dependencyName' | 'dependencyVersion' || 'dependencyGroup' | 'dependencyName' | 'dependencyVersion'
            null              | 'dependencyName' | 'dependencyVersion' || ''                | 'dependencyName' | 'dependencyVersion'
            'dependencyGroup' | 'dependencyName' | null                || 'dependencyGroup' | 'dependencyName' | ''
    }
}
