package org.danilopianini.gradle.latex

import java.io.File
import java.io.Serializable

/**
 * Represents a TeX artifact which will can be compiled into a PDF.
 * Used to maintain a graph of dependencies.
 *
 * It requires the artifact [name],
 * the [tex] root,
 * the [aux] file,
 * the resulting [pdf],
 * the bibliography generated file [bbl],
 * the list of [imageFiles],
 * the compilation [extraArgs],
 * the [diffs],
 * and the files this artifact is [watching].
 *
 */
data class LatexArtifact(
    val name: String,
    /**
     * Represents tex file which is used to call pdflatex.
     * Must be set.
     */
    val tex: File,
    val aux: File,
    val pdf: File,
    val bbl: File,
    /**
     * Collection of image files or directories with images
     * which have to be transformed because LaTeX cannot use them directly (e.g. svg, emf).
     * These are transformed to PDFs which then can be included in pdflatex.
     */
    val imageFiles: Iterable<File>,
    /**
     * Extra arguments to be passed to pdflatex when building this artifact.
     */
    val extraArgs: Iterable<String>,
    /**
     * Differential documents to get produced.
     */
    val diffs: Iterable<Int>,
    val watching: Iterable<Any>
) : Serializable {
    init {
        if ("/\\:<>\"?*|".any { it in name }) {
            throw IllegalStateException("Illegal name $name")
        }
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
