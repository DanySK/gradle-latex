package org.danilopianini.gradle.latex

import org.gradle.api.tasks.Console
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files
import javax.inject.Inject

open class BibtexTask @Inject constructor(artifact: LatexArtifact) : LatexTask(artifact) {

    val auxContent @Input get() = artifact.aux.readText()

    val bbl @OutputFile get() = project.file(artifact.bbl)

    @Console
    override fun getDescription() = "Uses BibTex to compile ${artifact.aux} into ${artifact.name}.bbl"

    /**
     * Execute bibtex command for given latex artifact.
     */
    @TaskAction
    fun bibTex() {
        if (artifact.aux.exists()) {
            if (Files.lines(artifact.aux.toPath()).anyMatch { it.contains("""\citation""") }) {
                "${extension.bibTexCommand.get()} \"${artifact.aux.name}\"".runScript()
            } else {
                project.logger.warn("No citation in \"${artifact.aux.absolutePath}\", bibtex not invoked.")
            }
        } else {
            project.logger.warn(
                "${artifact.aux.absolutePath} does not exist, cannot invoke ${extension.bibTexCommand.get()}"
            )
        }
    }
}
