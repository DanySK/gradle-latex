package org.danilopianini.gradle.latex

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.kotlin.dsl.get
import java.io.File
import java.io.PrintWriter
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

abstract class LatexTask : DefaultTask() {
    /**
     * Latex artifact used to run current task.
     */
    lateinit var artifact: LatexArtifact
    val extension: LatexExtension = project.extensions.get(Latex.EXTENSION_NAME) as LatexExtension

    init {
        group = Latex.TASK_GROUP
        logging.captureStandardError(LogLevel.ERROR)
        logging.captureStandardOutput(LogLevel.ERROR)
    }
    /**
     * Collection of all files whose change should trigger this task.
     * Collected for Gradle's continuous build feature.
     * Contains the following (based on the Latex artifact):
     * - main TeX file
     * - bib file, if there is one
     * - outputs (PDF) of dependent TeX files
     * - auxiliary files/folders
     */
    @InputFiles
    open fun inputFiles(): FileCollection = project.files(*artifact.flattenDependencies().toTypedArray())

    /**
     * Output of current task. Not used by task itself.
     * Set for Gradle's continuous build feature.
     */
    @OutputFile
    open fun pdf() = artifact.pdf

    fun String.runScript(
        terminalEmulator: String = extension.terminalEmulator.get(),
        from: File = project.projectDir,
        waitTime: Long = extension.waitTime.get(),
        waitUnit: TimeUnit = extension.waitUnit.get(),
        whenFails: (Int, String, String) -> Unit = { exit, stdout, stderr ->
            throw GradleException("Process $this terminated with non-zero value $exit.\nStandard error: \n$stderr\n\nStandard output:\n$stdout")
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
            Latex.LOG.debug("Launching {} in {} from directory {}, waiting up to {} {} for termination.", this, terminalEmulator, from, waitTime, waitUnit);
            it.println("$this \n exit")
        }
        val terminatedNaturally = shell.waitFor(waitTime, waitUnit)
        if (!terminatedNaturally) {
            throw GradleException("Process $this stalled and was forcibly terminated due to timeout. If the process was not interactive, consider using longer timeouts.")
        }
        shell.exitValue().takeIf { it != 0 }?.let { whenFails (it, stderr.readText(), stdout.readText()) }
        Latex.LOG.debug("{} completed successfully", this)
    }

}