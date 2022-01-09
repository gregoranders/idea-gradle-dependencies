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
package com.github.gregoranders.idea.gradle.dependencies.gradle.tooling

import org.gradle.api.Project
import org.gradle.tooling.provider.model.ToolingModelBuilder
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry
import spock.lang.*

@Title('Dependencies plugin')
@Narrative('''
> # As a user I would like to be able inject a [Plugin][plugin-url] into a [Gradle][gradle-url] build to collect dependencies.

[gradle-url]: https://gradle.org
[plugin-url]: https://docs.gradle.org/current/javadoc/org/gradle/api/Plugin.html
''')
@Subject(DependenciesPlugin)
@See([
    'https://gradle.org',
    'https://docs.gradle.org/current/javadoc/org/gradle/api/Plugin.html',
    'https://docs.gradle.org/current/javadoc/org/gradle/tooling/provider/model/ToolingModelBuilderRegistry.html',
    'https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/com/github/gregoranders/idea/gradle/dependencies/gradle/tooling/DependenciesPlugin.java'
])
@Issue([
    '5'
])
class DependenciesPluginSpec extends Specification {

    def 'should register plugin to registry'() {
        given: 'a mocked registry'
            ToolingModelBuilderRegistry toolingModelBuilderRegistry = Mock()
        and: 'a plugin using this registry'
            @Subject
            DependenciesPlugin testSubject = new DependenciesPlugin(toolingModelBuilderRegistry)
        when: 'the apply method is invoked on the plugin'
            testSubject.apply(_ as Project)
        then: 'the plugin should register a model builder to the registry'
            interaction {
                1 * toolingModelBuilderRegistry.register(_) >> { ToolingModelBuilder builder -> builder instanceof DependenciesModelBuilder }
            }
        and: 'no exception should be thrown'
            noExceptionThrown()
    }
}
