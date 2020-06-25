import de.fayard.dependencies.bootstrapRefreshVersionsAndDependencies
import org.danilopianini.VersionAliases.justAdditionalAliases
buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath("de.fayard:dependencies:+")
        classpath("org.danilopianini:refreshversions-aliases:+")
    }
}
bootstrapRefreshVersionsAndDependencies(justAdditionalAliases)

rootProject.name = "gradle-latex"
