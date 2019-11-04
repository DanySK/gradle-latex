package org.danilopianini.gradle.latex

import org.gradle.api.file.FileCollection
import java.io.File

/**
 * Represents a TeX artifact which will can be compiled into a PDF.
 * Used to maintain a graph of dependencies.
 *
 */
data class LatexArtifact @JvmOverloads constructor(
    val name: String,

    /**
     * Represents tex file which is used to call bibtex, pdflatex
     * Must be set.
     */
    val tex: File,

    val aux: File,

    val pdf: File,

    /**
     * Represents bib file used to call bibtex.
     */
    val bib: File? = null,

    /**
     * Collection of dependencies which have to be compiled
     * in order for this one to work (e.g. used with \input).
     */
    val dependsOn: Iterable<LatexArtifact> = emptyList(),

    /**
     * Collection of image files or directories with images
     * which have to be transformed because LaTeX cannot use them directly (e.g. svg, emf).
     * These are transformed to PDFs which then can be included in pdflatex.
     */
    val imageFiles: Iterable<File> = emptyList(),

    /**
     * Extra arguments to be passed to pdflatex when building this artifact.
     */
    val extraArgs: Iterable<String> = listOf(),

    /**
     * Differential documents to get produced.
     */
    val diffs: Iterable<Int> = emptyList(),

    val quiet: Boolean = true

) {
    fun flattenDependencies(): List<File> = listOf(tex, bib, aux).filterNotNull() + imageFiles + dependsOn.flatMap { it.flattenDependencies() }
}