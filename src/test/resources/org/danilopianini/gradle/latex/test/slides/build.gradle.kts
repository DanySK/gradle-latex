plugins {
    id ("org.danilopianini.gradle-latex") version "0.1.0" // Exemplificatory, pick the last stable one!
}

latex {
    (projectDir.listFiles() ?: emptyArray())
        .asSequence()
        .filter { it.isDirectory }
        .map { folder ->
            folder?.listFiles { file: File ->
                file.extension == "tex" && file.name.take(2) == folder.name.take(2)
            }
            ?.firstOrNull()
        }
        .filterNotNull()
        .map { it.relativeTo(projectDir) }
        .sortedBy { it.name }
        .forEach {
            it.path()
        }
}
