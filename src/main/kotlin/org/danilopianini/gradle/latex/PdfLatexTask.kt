package org.danilopianini.gradle.latex

import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 * Gradle task to run pdflatex on a TeX file.
 * One such task is created for each Latex artifact.
 *
 * @author csabasulyok
 */
open class PdfLatexTask @Inject constructor(artifact: LatexArtifact) : LatexTask(artifact) {

    @Console
    override fun getDescription() = "Uses pdflatex to compile ${artifact.tex} into ${artifact.pdf}"

    /**
     * Collection of all files whose change should trigger this task.
     * Collected for Gradle's continuous build feature.
     * Contains the following (based on the Latex artifact):
     * - main TeX file
     * - bib files, if there is one
     * - outputs (PDF) of dependent TeX files
     * - auxiliary files/folders
     */
    open val inputFiles: FileCollection
        @InputFiles get() = project.fileTree(project.rootDir)
            .filter { it.extension in artifact.trackedExtensions && it !in outputFiles }

    /**
     * Output of current task. Not used by task itself.
     * Set for Gradle's continuous build feature.
     */
    @OutputFiles
    open val outputFiles = project.files(artifact.pdf, artifact.aux)

    /**
     * Main task action.
     * Empties auxiliary directory.
     */
    @TaskAction
    fun pdfLatex() {
        Latex.LOG.info("Executing ${extension.pdfLatexCommand.get()} for {}", artifact.tex)
        val command = StringBuilder(extension.pdfLatexCommand.get())
            .append(artifact.extraArgs.joinToString(separator = " ", prefix = " "))
            .append(" \"")
            .append(artifact.tex.absolutePath)
            .append('\"')
            .toString()
        Latex.LOG.debug("Prepared command {}", command)
        command.runScript()
        command.runScript()
    }

    companion object {
        val trackedExtensions = listOf("bib", "eps", "jpeg", "jpg", "pdf", "png", "svg", "tex")
    }
}
