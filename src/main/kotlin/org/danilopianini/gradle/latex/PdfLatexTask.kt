package org.danilopianini.gradle.latex

import org.gradle.api.tasks.Console
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 * Gradle task to run pdflatex on a TeX file.
 * One such task is created for each Latex artifact.
 *
 * @author csabasulyok
 */
open class PdfLatexTask @Inject constructor(artifact: LatexArtifact) : LatexTask(artifact) {

  @Console
  override fun getDescription() = "Uses pdflatex to compile ${artifact.tex} into ${artifact.pdf}"

  /**
   * Main task action.
   * Empties auxiliary directory.
   */
  @TaskAction
  fun pdfLatex() {
    Latex.LOG.info("Executing ${extension.pdfLatexCommand.get()} for {}", artifact.tex)
    val command = StringBuilder(extension.pdfLatexCommand.get())
      .append(if (artifact.quiet) " -quiet " else " ")
      .append(artifact.extraArgs.joinToString(" "))
      .append(' ')
      .append(artifact.tex.absolutePath)
      .toString()
    Latex.LOG.debug("Prepared command {}", command)
    command.runScript()
    command.runScript()
  }
}
