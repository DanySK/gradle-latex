package org.danilopianini.gradle.latex

import org.gradle.api.Project
import java.io.File

data class LatexDSL(private val project: Project, val name: String) {
    var bib: String? = with(project) {
        file(fromName("bib"))
            .takeIf { it.exists() }
            ?.absolutePath
    }
    var dependsOn: Iterable<LatexArtifact> = emptyList()
    var images: Iterable<File> = emptyList()
    var extraArguments: Iterable<String> = listOf("-shell-escape", "-synctex=1", "-interaction=nonstopmode", "-halt-on-error")
    private fun fromName(extension: String) = when {
        name.endsWith(".tex") -> name.substring(0, endIndex = name.length - 4) + ".$extension"
        else -> "$name.$extension"
    }
    fun fileFromName(extension: String) = project.file(fromName(extension))
}
