package org.danilopianini.gradle.latex

import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files
import javax.inject.Inject

open class BibtexTask @Inject constructor(artifact: LatexArtifact) : LatexTask(artifact) {

    @InputFiles
    override val inputFiles: FileCollection = project.files(
        *(listOfNotNull(artifact.aux, artifact.tex, artifact.bib).toTypedArray()))

    @Console
    override fun getDescription() = "Uses BibTex to compile ${artifact.aux} into ${artifact.name}.bbl"

    /**
     * Execute bibtex command for given latex artifact.
     */
    @TaskAction
    fun bibTex() {
        if (!artifact.aux.exists()) {
            throw GradleException("${artifact.aux.absolutePath} does not exist," +
                    " cannot invoke ${extension.bibTexCommand.get()}")
        }
        if (Files.lines(artifact.aux.toPath()).anyMatch { it.contains("""\citation""") }) {
            "${extension.bibTexCommand.get()} ${artifact.aux.name}".runScript()
        } else {
            Latex.LOG.warn("No citation in ${artifact.aux.absolutePath}, bibtex not invoked.")
        }
    }
}
