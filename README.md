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
    quiet.set(true)
    terminalEmulator.set("bash")
    waitTime.set(1)
    waitUnit.set(TimeUnit.MINUTES)
    pdfLatexCommand.set("pdflatex")
    bibTexCommand.set("bibtex")
    "myMainLatexFile"() {
        dependsOn = emptyList<LatexArtifact>()
        extraArguments = listOf("-shell-escape", "-synctex=1", "-interaction=nonstopmode", "-halt-on-error")
        quiet = null // Inherits global setting
    }
}
```

#### Building multiple projects in order

The `dependsOn` option can be used to specify dependencies among latex tasks.
In the following example, project1 and project2 can get built in parallel,
while project3 build can start only after both the former completed successfully.

```kotlin
latex {
    val project1 = "project1"()
    val project2 = "project2"()
    "project3" {
        dependsOn = setOf(project1, project2)
    }
}
```

## Contributing to the project

I gladly review pull requests and I'm happy to improve the work.
If the software was useful to you, please consider supporting my development activity
[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donate_SM.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=5P4DSZE5DV4H2&currency_code=EUR)


