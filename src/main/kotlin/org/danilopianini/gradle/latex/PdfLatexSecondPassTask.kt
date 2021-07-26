package org.danilopianini.gradle.latex

import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import javax.inject.Inject

/**
 * Re-runs pdflatex.
 */
open class PdfLatexSecondPassTask @Inject constructor(artifact: LatexArtifact) : PdfLatexTask(artifact) {
    override val inputFiles: FileCollection
        @InputFiles get() = project.files(artifact.bbl)
}
