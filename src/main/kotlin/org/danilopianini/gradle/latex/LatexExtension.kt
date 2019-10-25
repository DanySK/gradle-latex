package org.danilopianini.gradle.latex

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.register
import java.util.concurrent.TimeUnit

inline fun <reified T> Project.propertyWithDefault(default: T): Property<T> =
  objects.property(T::class.java).apply { convention(default) }

inline fun <reified T> Project.propertyWithDefault(noinline default: () -> T): Property<T> =
  objects.property(T::class.java).apply { convention( default()) }

/**
 * Gradle extension to create new dynamic tasks & maintain and manage latex artifacts.
 * Registered to Gradle as extension in LatexPlugin. Thereafter the instance can be accessed via project.latex
 *
 */
open class LatexExtension @JvmOverloads constructor(
  private val project: Project,
  // val auxDir: Property<File> = project.propertyWithDefault(project.file(".gradle/latex-temp")),
  /**
   * Utilities for easy execution.
   * After adding extension, can be accessed via project.latex.utils
   */
  val quiet: Property<Boolean> = project.propertyWithDefault(true),
  val terminalEmulator: Property<String> = project.propertyWithDefault("bash"),
  val waitTime: Property<Long> = project.propertyWithDefault(1),
  val waitUnit: Property<TimeUnit> = project.propertyWithDefault(TimeUnit.MINUTES),
  val pdfLatexCommand: Property<String> = project.propertyWithDefault("pdflatex"),
  val bibTexCommand: Property<String> = project.propertyWithDefault("bibtex"),
  val inkscapeCommand: Property<String> = project.propertyWithDefault("inkscape"),
  val gitLatexdiffCommand: Property<String> = project.propertyWithDefault("git latexdiff")
) {

  val runAll by project.tasks.register<DefaultTask>("buildLatex") {
    group = Latex.TASK_GROUP
    description = "Run all LaTeX tasks"
  }

  @JvmOverloads operator fun String.invoke(configuration: LatexArtifactBuilder.() -> Unit = { }): LatexArtifact = LatexArtifactBuilder(project, this)
    .also(configuration)
    .let { builder ->
      LatexArtifact(
        this,
        tex = project.file(with(builder.name) { if (endsWith(".tex")) this else "$this.tex" }),
        bib = builder.bib?.let { project.file(it) } ?.takeIf { it.exists() },
        pdf = builder.fileFromName("pdf"),
        aux = builder.fileFromName("aux"),
        dependsOn = builder.dependsOn,
        imageFiles = builder.images,
        extraArgs = builder.extraArguments,
        quiet = builder.quiet ?: quiet.get()
      )
    }.also {
      Latex.LOG.debug("Produced {}", it)
      // create new task and set its properties using the artifact
      fun LatexArtifact.buildName() = "buildLatex.${name}"
      val completionTask by project.tasks.register<LatexTask>(it.buildName()) {
        artifact = it
        description = "Builds a LaTeX project"
      }
      runAll.dependsOn(completionTask)
      val pdfLatexTask by project.tasks.register<PdfLatexTask>("pdfLatex.${this}") {
        artifact = it
        dependsOn(artifact.dependsOn.map { project.task(it.buildName()) })
      }
      completionTask.dependsOn(pdfLatexTask)
      if (it.bib != null) {
        val bibTexTask by project.tasks.register<BibtexTask>("bibtex.${this}") {
          artifact = it
          dependsOn(pdfLatexTask)
        }
        val pdfLatexPass2 by project.tasks.register<PdfLatexTask>("pdfLatexAfterBibtex.${this}") {
          artifact = it
          dependsOn(bibTexTask)
        }
        completionTask.dependsOn(pdfLatexPass2)
      }
    }
}
