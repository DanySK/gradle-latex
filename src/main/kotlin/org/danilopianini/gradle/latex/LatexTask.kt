package org.danilopianini.gradle.latex

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.kotlin.dsl.get
import java.io.File
import java.io.PrintWriter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

abstract class LatexTask @Inject constructor(@Input protected val artifact: LatexArtifact) : DefaultTask() {
    /**
     * Latex artifact used to run current task.
     */
    @Internal
    protected val extension: LatexExtension = project.extensions.get(Latex.EXTENSION_NAME) as LatexExtension

    init {
        group = Latex.TASK_GROUP
        logging.captureStandardError(LogLevel.ERROR)
        logging.captureStandardOutput(LogLevel.ERROR)
    }

    fun String.runScript(
        terminalEmulator: String = extension.terminalEmulator.get(),
        from: File = artifact.tex.parentFile,
        waitTime: Long = extension.waitTime.get(),
        waitUnit: TimeUnit = extension.waitUnit.get(),
        whenFails: (Int, String, String) -> Unit = { exit, stdout, stderr ->
            throw GradleException(
                """
                |Process $this terminated with non-zero value $exit.
                |
                |Standard error:
                |$stderr
                |
                |Standard output:
                |$stdout
                """.trimMargin()
            )
        }
    ) {
        val stderr = createTempFile()
        val stdout = createTempFile()
        val shell = ProcessBuilder(terminalEmulator)
            .directory(from)
            .redirectError(stderr)
            .redirectOutput(stdout)
            .start()
        shell.inputStream.copyTo(System.out)
        PrintWriter(shell.outputStream).use {
            project.logger.debug(
                "Launching {} in {} from directory {}, waiting up to {} {} for termination.",
                this, terminalEmulator, from, waitTime, waitUnit
            )
            it.println("$this \n exit")
        }
        val terminatedNaturally = shell.waitFor(waitTime, waitUnit)
        if (!terminatedNaturally) {
            throw GradleException(
                "Process $this stalled and was forcibly terminated due to timeout." +
                    " If the process was not interactive, consider using longer timeouts."
            )
        }
        shell.exitValue().takeIf { it != 0 }?.let { whenFails(it, stderr.readText(), stdout.readText()) }
        project.logger.debug("{} completed successfully", this)
    }
}
