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
package io.github.gregoranders.idea.gradle.dependencies.gradle.utilities

import org.gradle.internal.impldep.org.apache.commons.io.FilenameUtils
import spock.lang.*

import java.nio.file.Files
import java.nio.file.Path

@Title('Init script')
@Narrative('''
> # As a user I would like to be able inject a [Script][script-url] into a [Gradle][gradle-url].

[gradle-url]: https://gradle.org
[script-url]: https://docs.gradle.org/current/userguide/init_scripts.html
''')
@Subject(InitScriptInjector)
@See([
    'https://gradle.org',
    'https://docs.gradle.org/current/userguide/init_scripts.html',
    'https://github.com/gregoranders/idea-gradle-dependencies/blob/main/src/main/java/io/github/gregoranders/idea/gradle/dependencies/gradle/utilities/InitScriptInjector.java'
])
@Issue([
    '5', '12'
])
class InitScriptInjectorSpec extends Specification {

    def 'should return temporary init script path with replaced plugin path'() {
        given: 'a valid init script'
            def initScript = '/gradle-dependencies-plugin.gradle'
        and: 'the unit under test is provided this init script'
            @Subject
            InitScriptInjector testSubject = new InitScriptInjector(initScript, 'plugin')
        when: 'a temporary init script is requested'
            def path = testSubject.getAbsolutePath()
        then: 'the script exists'
            Files.exists(Path.of(path))
        and: 'it contains the replaced path to the plugin'
            def lines = Files.readAllLines(Path.of(path))
            checkPluginPath(lines)
        and: 'it contains the replaced plugin'
            checkPluginName(lines, 'plugin')
        and: 'close is invoked on the testSubject'
            testSubject.close()
        and: 'no exceptions are thrown'
            noExceptionThrown()
    }

    def 'should remove temporary init script when close is invoked'() {
        given: 'a valid init script'
            def initScript = '/gradle-dependencies-plugin.gradle'
        and: 'the unit under test is provided this init script'
            @Subject
            InitScriptInjector testSubject = new InitScriptInjector(initScript, 'test')
        when: 'a temporary init script is requested'
            def path = testSubject.getAbsolutePath()
        and: 'the script exists'
            Files.exists(Path.of(path))
        and: 'close is invoked on the testSubject'
            testSubject.close()
        then: 'the temporary init script should be deleted'
            !Files.exists(Path.of(path))
        and: 'no exceptions are thrown'
            noExceptionThrown()
    }

    def 'should not create a temporary init script'() {
        given: 'a valid init script'
            def initScript = '/gradle-dependencies-plugin.gradle'
        and: 'the unit under test is provided this init script'
            @Subject
            InitScriptInjector testSubject = new InitScriptInjector(initScript, 'test')
        when: 'close is invoked on the testSubject'
            testSubject.close()
        then: 'no exceptions are thrown'
            noExceptionThrown()
    }

    def 'should throw exception when not existent init script is provided'() {
        given: 'an invalid init script'
            def initScript = '/test.gradle'
        and: 'the unit und test provided this init script'
            @Subject
            InitScriptInjector testSubject = new InitScriptInjector(initScript, 'test')
        when: 'a temporary init scrip is requested'
            testSubject.getAbsolutePath()
        then: 'an exception should be thrown'
            thrown(NullPointerException)
    }

    def checkPluginPath(List<String> lines) {
        boolean found = false
        lines.forEach(line -> {
            if (line.contains(FilenameUtils.separatorsToUnix("/build/classes/java/main/')"))) {
                found = true
            }
        })
        found
    }

    def checkPluginName(List<String> lines, String plugin) {
        boolean found = false
        lines.forEach(line -> {
            if (line.contains("apply plugin: ${plugin}")) {
                found = true
            }
        })
        found
    }
}
