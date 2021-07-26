package org.danilopianini.gradle.latex

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.register
import java.util.concurrent.TimeUnit
import java.io.Serializable

private inline fun <reified T> Project.propertyWithDefault(default: T): Property<T> =
    objects.property(T::class.java).apply { convention(default) }

private inline fun <reified T> Project.propertyWithDefault(noinline default: () -> T): Property<T> =
    objects.property(T::class.java).apply { convention(default()) }

/**
 * Gradle extension to create new dynamic tasks & maintain and manage latex artifacts.
 * Registered to Gradle as extension in LatexPlugin. Thereafter the instance can be accessed via project.latex.
 *
 * Allows for configuring:
 *   - [terminalEmulator], defaulting to bash,
 *   - [waitTime], time after which a command is considered stuck,
 *   - [waitUnit], the unit of measure of the previous time,
 *   - [pdfLatexCommand], the command for compiling latex,
 *   - [bibTexCommand], the command for producing the bbl files,
 *   - [inkscapeCommand] (currently unused),
 *   - [gitLatexdiffCommand] (currently unused).
 */
open class LatexExtension @JvmOverloads constructor(
    private val project: Project,
    val terminalEmulator: Property<String> = project.propertyWithDefault("bash"),
    val waitTime: Property<Long> = project.propertyWithDefault(1),
    val waitUnit: Property<TimeUnit> = project.propertyWithDefault(TimeUnit.MINUTES),
    val pdfLatexCommand: Property<String> = project.propertyWithDefault("pdflatex"),
    val bibTexCommand: Property<String> = project.propertyWithDefault("bibtex"),
    val inkscapeCommand: Property<String> = project.propertyWithDefault("inkscape"),
    val gitLatexdiffCommand: Property<String> = project.propertyWithDefault("git latexdiff")
) : Serializable {

    /**
     * Runs all Gradle-LaTeX tasks.
     */
    val runAll by project.tasks.register<DefaultTask>("buildLatex") {
        group = Latex.TASK_GROUP
        description = "Run all LaTeX tasks"
    }

    /**
     * DSL component making strings invokable.
     */
    @JvmOverloads operator fun String.invoke(configuration: LatexDSL.() -> Unit = { }): LatexArtifact =
        LatexDSL(project, this)
            .also(configuration)
            .let { builder ->
                LatexArtifact(
                    this.replace("""[\/\\:<>"?\*|]""".toRegex(), "-"),
                    tex = project.file(with(builder.name) { if (endsWith(".tex")) this else "$this.tex" }),
                    pdf = builder.fileFromName("pdf"),
                    aux = builder.fileFromName("aux"),
                    bbl = builder.fileFromName("bbl"),
                    imageFiles = builder.images,
                    extraArgs = builder.extraArguments,
                    watching = builder.watching,
                    diffs = emptyList()
                )
            }
            .also { artifact ->
                project.logger.debug("Produced {}", artifact)
                // All compilation tasks of this document
                val buildName = "buildLatex.${artifact.name}"
                val completionTask by project.tasks.register<LatexTask>(buildName, artifact)
                completionTask.description = "Builds LaTeX project ${artifact.name}"
                runAll.dependsOn(completionTask)
                // pdflatex, first run
                val pdfLatexTask by project.tasks.register<PdfLatexTask>("pdfLatex.${artifact.name}", artifact)
                completionTask.dependsOn(pdfLatexTask)
                // bibtex
                val bibTexTask by project.tasks.register<BibtexTask>("bibtex.${artifact.name}", artifact)
                bibTexTask.dependsOn(pdfLatexTask)
                // pdflatex, second run
                val pdfLatexPass2 by project.tasks.register<PdfLatexSecondPassTask>(
                    "pdfLatexAfterBibtex.${artifact.name}",
                    artifact
                )
                pdfLatexPass2.dependsOn(bibTexTask)
                completionTask.dependsOn(pdfLatexPass2)
            }

    companion object {
        private const val serialVersionUID = 1L
    }
}
