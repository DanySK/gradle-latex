package org.danilopianini.gradle.latex.test

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.source.yaml
import io.github.classgraph.ClassGraph
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.file.shouldBeAFile
import org.gradle.internal.impldep.org.junit.rules.TemporaryFolder
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.slf4j.LoggerFactory
import java.io.File

fun folder(closure: TemporaryFolder.() -> Unit) = TemporaryFolder().apply {
    create()
    closure()
}
fun TemporaryFolder.file(name: String, content: () -> String) = newFile(name).writeText(content().trimIndent())

data class Configuration(val tasks: List<String>, val options: List<String>) {
    companion object : ConfigSpec() {
        val tasks by required<List<String>>()
        val options by optional<List<String>>(emptyList())
    }
}
data class Expectation(val file_exists: List<String>, val success: List<String>, val failure: List<String>) {
    companion object : ConfigSpec() {
        val file_exists by optional<List<String>>(emptyList())
        val success by optional<List<String>>(emptyList())
        val failure by optional<List<String>>(emptyList())
    }
}
data class Test(val description: String, val configuration: Configuration, val expectation: Expectation) {
    companion object : ConfigSpec() {
        val description by required<String>()
        val configuration by required<Configuration>()
        val expectation by required<Expectation>()
    }
}
object Root : ConfigSpec("") {
    val tests by required<List<Test>>()
}

class LatexTests : StringSpec({
    val pluginClasspathResource = ClassLoader.getSystemClassLoader()
        .getResource("plugin-classpath.txt")
        ?: throw IllegalStateException("Did not find plugin classpath resource, run \"testClasses\" build task.")
    val classpath = pluginClasspathResource.openStream().bufferedReader().use { reader ->
        reader.readLines().map { File(it) }
    }
    val scan = ClassGraph()
        .enableAllInfo()
        .acceptPackages("org.danilopianini.gradle.latex.test")
        .scan()
    scan.getResourcesWithLeafName("test.yaml")
        .flatMap {
            log.debug("Found test list in $it")
            val yamlFile = File(it.classpathElementFile.absolutePath + "/" + it.path)
            val testConfiguration = Config {
                addSpec(Root)
                addSpec(Test)
                addSpec(Expectation)
                addSpec(Configuration)
            }.from.yaml.inputStream(it.open())
            testConfiguration[Root.tests].map { it to yamlFile.parentFile }
        }.forEach { (test, location) ->
            log.debug("Test to be executed: $test from $location")
            val testFolder = folder {
                location.copyRecursively(this.root)
            }
            log.debug("Test has been copied into $testFolder and is ready to get executed")
            test.description {
                val result = GradleRunner.create()
                    .withProjectDir(testFolder.root)
                    .withPluginClasspath(classpath)
                    .withArguments(test.configuration.tasks + test.configuration.options)
                    .build()
                println(result.tasks)
                println(result.output)
                test.expectation.success.forEach {
                    val task = result.task(":$it")
                    require(task != null) {
                        "Task $it was not present among the executed tasks"
                    }
                    task.outcome shouldBe TaskOutcome.SUCCESS
                }
                test.expectation.file_exists.forEach {
                    with(File("${testFolder.root.absolutePath}/$it")) {
                        shouldExist()
                        shouldBeAFile()
                    }
                }
            }
        }
}) {
    companion object {
        val log = LoggerFactory.getLogger(LatexTests::class.java)
    }
}
