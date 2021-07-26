package org.danilopianini.gradle.latex

import org.gradle.api.Project
import java.io.File

/**
 * Entry point for the LaTeX DSL, given a roott file [name].
 */
data class LatexDSL(private val project: Project, val name: String) {

    /**
     * The list of images.
     */
    var images: Iterable<File> = emptyList()

    /**
     * The arguments to be passed to pdfLatex (or to the selected command).
     */
    var extraArguments: Iterable<String> = listOf(
        "-shell-escape",
        "-synctex=1",
        "-interaction=nonstopmode",
        "-halt-on-error"
    )

    /**
     * The list of files and folders being watched.
     */
    var watching: Iterable<Any> = emptyList()

    private fun fromName(extension: String) = when {
        name.endsWith(".tex") -> name.substring(0, endIndex = name.length - 4) + ".$extension"
        else -> "$name.$extension"
    }

    /**
     * Shortcut for [Project.file].
     */
    fun fileFromName(extension: String) = project.file(fromName(extension))
}
