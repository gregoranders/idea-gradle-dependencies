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
package io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.mapper

import io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Configuration
import io.github.joke.spockmockable.Mockable
import org.gradle.api.artifacts.DependencySet
import spock.lang.*

import java.util.stream.Stream

@Narrative('''
> # As a user I would like to map a [Gradle][gradle-url] [configuration][configuration-url] to a [model][model-url] using a custom [mapper][mapper-url].

[gradle-url]: https://gradle.org
[configuration-url]: https://docs.gradle.org/current/javadoc/org/gradle/api/artifacts/Configuration.html
[model-url]: https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/io/github/gregoranders/idea/gradle/dependencies/gradle/tooling/model/api/Configuration.java
[mapper-url]: https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/io/github/gregoranders/idea/gradle/dependencies/gradle/tooling/model/mapper/ConfigurationMapper.java
''')
@Subject([ConfigurationMapper, Configuration])
@See([
    'https://docs.gradle.org/current/javadoc/org/gradle/api/artifacts/Configuration.html',
    'https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/io/github/gregoranders/idea/gradle/dependencies/gradle/tooling/model/api/Configuration.java',
    'https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/io/github/gregoranders/idea/gradle/dependencies/gradle/tooling/model/mapper/ConfigurationMapper.java',
])
@Issue([
    '40'
])
@Mockable(ConfigurationMapper)
class ConfigurationMapperSpec extends Specification {

    def dependencyMapper = new DependencyMapper()

    def dependencySetMapper = new DependencySetMapper(dependencyMapper)

    @Subject
    ConfigurationMapper testSubject = new ConfigurationMapper(dependencySetMapper)

    def 'should map a gradle configuration'() {
        given: 'a mocked gradle configuration'
            def mock = Mock(org.gradle.api.artifacts.Configuration)
        and: 'a mocked empty gradle dependency set'
            def mockedDependencySet = Mock(DependencySet)
        when: 'the unit under test maps this configuration'
            def model = testSubject.map(mock)
        then: 'following interactions should be executed'
            interaction {
                1 * mock.getName() >> 'configurationName'
                1 * mock.getDependencies() >> mockedDependencySet
                1 * mockedDependencySet.stream() >> Stream.empty()
            }
        and: 'the returned model should be of the expected type'
            model instanceof Configuration
        and: 'have the expected name'
            model.name() == 'configurationName'
        and: 'no exceptions should be thrown'
            noExceptionThrown()
    }
}
