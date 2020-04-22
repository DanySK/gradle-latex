# gradle-latex
A Gradle plugin for building LaTeX projects

## Rationale
I wanted something to smootly build latex in CI without having to fiddle with bash

## Usage

### Importing the plugin

```kotlin
plugins {
    id ("org.danilopianini.gradle-latex") version "0.1.0" // Exemplificatory, pick the last stable one!
}
```

### Configuring the plugin

#### Minimal configuration

```kotlin
latex {
    "myMainLatexFile"()
}
```

This configuration guesses your main `tex` file to be `myMainLatexFile.tex`, autodetects if a `myMainLatexFile.bib` is
present, and builds the resulting `myMainLatexFile.pdf`.

#### Building multiple main files

```kotlin
latex {
    "oneFile"()
    "anotherFile"()
}
```

You can have one entry in the latex section of your `build.gradle.kts` per project you want to build.
In case your files are spread across multiple directories,
consider each sub-directory a subproject and configure Gradle accordingly.

See for instance the [example with Elsevier's CAS LaTeX template](https://github.com/DanySK/gradle-latex/tree/master/src/test/resources/org/danilopianini/gradle/latex/test/elsevier-cas),
in particular look at `settings.gradle.kts` and `build.gradle.kts` both in the root and in the `doc` folder

#### Configuration options

Global options can be specified directly in the latex block.
Per-project options can be specified in a block after the main file name.
The following examples shows the available options and their default values.

```kotlin
latex {
    terminalEmulator.set("bash") // Your terminal
    waitTime.set(1) // How long before considering a process stalled
    waitUnit.set(TimeUnit.MINUTES) // Time unit for the number above
    pdfLatexCommand.set("pdflatex")
    bibTexCommand.set("bibtex")
    "myMainLatexFile" {
        // Options for pdflatex
        extraArguments = listOf("-shell-escape", "-synctex=1", "-interaction=nonstopmode", "-halt-on-error")
        /*
         * Additional files and directories whose change should trigger a build in case gradle is used with -t flag.
         * Can be passed files, strings, or any Object compatible with Gradle's project.files
         */
        watching = emptyList()
    }
    "anotherMainLatexFile"()
}
```

## Contributing to the project

I gladly review pull requests and I'm happy to improve the work.
If the software was useful to you, please consider supporting my development activity
[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=5P4DSZE5DV4H2&currency_code=EUR)


