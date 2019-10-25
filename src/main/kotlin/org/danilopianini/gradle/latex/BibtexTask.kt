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
    override fun inputFiles(): FileCollection = project.files(artifact.bib)

    override fun getDescription() =  "Uses BibTex to compile ${artifact.bib} into ${artifact.name}.bbl"

    /**
     * Execute bibtex command for given latex artifact.
     *
     * @param artifact Any Latex artifact with the tex and bib properties set.
     */
    @TaskAction
    fun bibTex() {
        require(artifact.bib != null) {
            throw GradleException("Bibtex task cannot run on artifacts without a bib configured such as $artifact")
        }
        if (!artifact.aux.exists()) {
            throw GradleException("${artifact.aux.absolutePath} does not exist, cannot invoke ${extension.bibTexCommand.get()}")
        }
        if (Files.lines(artifact.aux.toPath()).anyMatch { it.contains("""\citation""") }) {
            val bib = artifact.bib!!
            if (!bib.exists()) {
                throw GradleException("${bib.absolutePath} does not exist, cannot invoke ${extension.bibTexCommand.get()}")
            }
            "${extension.bibTexCommand.get()} ${artifact.aux.name}".runScript()
        }
    }

}