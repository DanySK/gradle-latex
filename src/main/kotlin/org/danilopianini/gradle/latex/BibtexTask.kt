package org.danilopianini.gradle.latex

import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.withGroovyBuilder
import java.io.File
import java.nio.file.Files

open class BibtexTask : LatexTask() {

    @InputFiles
    override fun inputFiles(): FileCollection = project.files(
        *(listOf(artifact.aux, artifact.tex, artifact.bib).filterNotNull().toTypedArray()))

    override fun getDescription() =  "Uses BibTex to compile ${artifact.aux} into ${artifact.name}.bbl"

    /**
     * Execute bibtex command for given latex artifact.
     *
     * @param artifact Any Latex artifact with the tex and bib properties set.
     */
    @TaskAction
    fun bibTex() {
        if (!artifact.aux.exists()) {
            throw GradleException("${artifact.aux.absolutePath} does not exist, cannot invoke ${extension.bibTexCommand.get()}")
        }
        if (Files.lines(artifact.aux.toPath()).anyMatch { it.contains("""\citation""") }) {
            "${extension.bibTexCommand.get()} ${artifact.aux.name}".runScript()
        } else {
            Latex.LOG.warn("No citation in ${artifact.aux.absolutePath}, bibtex not invoked.")
        }
    }

}