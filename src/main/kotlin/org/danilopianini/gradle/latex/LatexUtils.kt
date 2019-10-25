package org.danilopianini.gradle.latex

import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.gradle.kotlin.dsl.*
import java.io.File

/**
 * Utility methods related to ant.
 *
 */
class LatexUtils @JvmOverloads constructor(
    val project: Project
) {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(LatexUtils::class.java)
    }


    /**
    * Execute bibtex command for given latex artifact.
    *
    * @param artifact Any Latex artifact with the tex and bib properties set.
    */
    fun bibTex(artifact: LatexArtifact, auxDir: File = project.projectDir) {
        LOG.info("Executing bibtex for {}", artifact.tex)
        require(artifact.bib != null) {
            "Cannot run BibTex on a null resource: ${artifact}"
        }
        project.ant.withGroovyBuilder {
            "copy"(
                "file" to artifact.bib.absolutePath,
                "todir" to auxDir.absolutePath,
                "overwrite" to "true",
                "force" to "true"
            )
            "exec"(
                "executable" to "bibtex",
                "dir" to auxDir.absolutePath,
                "failonerror" to "true"
            ) {
                "arg"("value" to artifact.tex)
            }
        }
   }

    // /**
    //  * Execute inkscape for an image file, to produce a pdf.
    //  *

    //  * @param imgFile source svg/emf file for inkscape
    //  * @param pdfFile target pdf file
    //  */
    // fun inkscape(imgFile: File, pdfFile: File) {
    //   p.ant.exec(executable: 'inkscape', dir: p.projectDir, failonerror: true) {
    //     arg(value: "--export-pdf=${pdfFile}")
    //     arg(value: imgFile)
    //   }
    // }

    // fun LatexExtension.copyOutput(obj: LatexArtifact) {
    //     val src = p.file("${auxDir}/${obj.pdf}")
    //     p.ant.withGroovyBuilder {
    //         "copy"(
    //             "file" to src.absolutePath,
    //             "tofile" to obj.pdf,
    //             "overwrite" to "true",
    //             "force" to "true"
    //         )
    //     }
    // }

//  /**
//   * Copies output of pdfLatex to final destination as described in a latex artifact.
//   * Should be called right after pdfLatex.
//   *
//   * @param obj Any Latex artifact with the name and pdf properties set.
//   */
//  void copyOutput(kt obj) {
//    File src = new File("${p.latex.auxDir}/${obj.name}.pdf")
//    LOG.quiet "Copying $src to ${obj.pdf}"
//    p.ant.copy(file: src, tofile:obj.pdf, overwrite:true, force:true)
//  }
//
//  /**
//   * Use ant to delete all files from a directory.
//   *
//   * @param dir Directory to empty
//   */
//  void emptyContent(File dir) {
//    LOG.quiet "Emptying content from folder $dir"
//    p.ant.delete {
//      fileset(dir: dir, includes:'**/*')
//    }
//  }
//
//
//  /**
//   * Reduces a FileCollection so that if directories are included,
//   * only unsupported image files (svg/emf) for inkscape are tagged.
//   */
//  FileCollection findImgFiles(Collection<String> fileNames) {
//    p.files(fileNames.collect { String imgFile ->
//      if (p.file(imgFile).directory) {
//        p.fileTree(dir: imgFile, include: ['**/*.svg', '**/*.emf'])
//      } else {
//        imgFile
//      }
//    })
//  }
//
//  /**
//   * Creates expected pdf file based on image file.
//   */
//  File imgFileToPdfFile(File imgFile) {
//    new File("$imgFile".take("$imgFile".lastIndexOf('.')) + '.pdf')
//  }
}
