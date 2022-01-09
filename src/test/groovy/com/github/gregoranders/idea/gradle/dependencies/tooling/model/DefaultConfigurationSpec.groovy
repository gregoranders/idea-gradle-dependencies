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

package com.github.gregoranders.idea.gradle.dependencies.tooling.model

import com.github.gregoranders.idea.gradle.dependencies.tooling.model.api.Configuration
import com.github.gregoranders.idea.gradle.dependencies.tooling.model.api.Dependency
import spock.lang.*

@Narrative('''
> # As a user I would like to be able to get a custom [model][model-url] from a [Gradle][gradle-url] build to collect dependencies.

[gradle-url]: https://gradle.org
[model-url]: https://github.com/bmuschko/tooling-api-custom-model
''')
@Subject([DefaultConfiguration, Configuration])
@See([
    'https://gradle.org',
    'https://github.com/bmuschko/tooling-api-custom-model',
    'https://docs.gradle.org/current/javadoc/org/gradle/api/artifacts/Configuration.html',
    'https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/com/github/gregoranders/idea/gradle/dependencies/tooling/model/api/Configuration.java',
    'https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/com/github/gregoranders/idea/gradle/dependencies/tooling/model/DefaultConfiguration.java'
])
@Issue([
    '5', '22'
])
class DefaultConfigurationSpec extends Specification {

    final String dependencyGroup = 'testGroup'

    final String dependencyName = 'testName'

    final String dependencyVersion = 'testVersion'

    final String configurationName = 'testConfiguration'

    Dependency dependency = new DefaultDependency(dependencyGroup, dependencyName, dependencyVersion)

    @Subject
    Configuration testSubject = new DefaultConfiguration(configurationName, Set.of(dependency))

    def 'should return expected configuration name'() {
        expect: 'name to equal "testConfiguration"'
            testSubject.name() == configurationName
    }

    @Issue('22')
    def 'should return a set with one dependency'() {
        expect: 'set of dependencies should have one element'
            testSubject.dependencies().size() == 1
    }

    @Issue('22')
    def 'should contain expected dependency'() {
        expect: 'should equal provided dependency'
            testSubject.dependencies()[0] == dependency
    }
}
