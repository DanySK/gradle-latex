package org.danilopianini.gradle.latex

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

/**
 * A Plugin configuring the project for publishing on Maven Central.
 */
class Latex : Plugin<Project> {
    companion object {
        /**
         * The name of the publication to be created.
         */
        const val TASK_GROUP = "LaTeX"

        /**
         * The extension name, entry point for the DSL.
         */
        const val EXTENSION_NAME = "latex"
    }

    override fun apply(project: Project) {
        project.extensions.create<LatexExtension>(EXTENSION_NAME, project)
    }
}
