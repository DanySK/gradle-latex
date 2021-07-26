import org.danilopianini.gradle.mavencentral.mavenCentral
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    id("org.danilopianini.git-sensitive-semantic-versioning")
    kotlin("jvm")
    id("com.gradle.plugin-publish")
    id("org.danilopianini.publish-on-central")
    id("org.jetbrains.dokka")
    id("kotlin-qa")
}

gitSemVer {
    buildMetadataSeparator.set("-")
}

group = "org.danilopianini"
val projectId = "$group.$name"
val fullName = "Gradle Latex Plugin"
val websiteUrl = "https://github.com/DanySK/gradle-latex"
val projectDetails = "A plugin for compiling LaTeX, inspired by https://github.com/csabasulyok/gradle-latex"
val pluginImplementationClass = "org.danilopianini.gradle.latex.Latex"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    testImplementation(gradleTestKit())
    testImplementation("io.github.classgraph:classgraph:_")
    testImplementation("com.uchuhimo:konf-yaml:_")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:_")
    testImplementation("io.kotest:kotest-assertions-core-jvm:_")
    testImplementation("io.kotest:kotest-property-jvm:_")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        allWarningsAsErrors = true
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

tasks.withType<Copy> {
    duplicatesStrategy = org.gradle.api.file.DuplicatesStrategy.WARN
}

publishOnCentral {
    projectDescription = projectDetails
    projectLongName = fullName
    repository("https://maven.pkg.github.com/danysk/gradle-latex") {
        user = "DanySK"
        password = System.getenv("GITHUB_TOKEN")
    }
}

tasks {
    "test"(Test::class) {
        useJUnitPlatform()
        testLogging.showStandardStreams = true
        testLogging {
            showCauses = true
            showStackTraces = true
            showStandardStreams = true
            events(*TestLogEvent.values())
            exceptionFormat = TestExceptionFormat.FULL
        }
    }
    register("createClasspathManifest") {
        val outputDir = file("$buildDir/$name")
        inputs.files(sourceSets.main.get().runtimeClasspath)
        outputs.dir(outputDir)
        doLast {
            outputDir.mkdirs()
            file("$outputDir/plugin-classpath.txt").writeText(sourceSets.main.get().runtimeClasspath.joinToString("\n"))
        }
    }
}

// Add the classpath file to the test runtime classpath
dependencies {
    testRuntimeOnly(files(tasks["createClasspathManifest"]))
}

publishing {
    publications {
        withType<MavenPublication> {
            pom {
                developers {
                    developer {
                        name.set("Danilo Pianini")
                        email.set("danilo.pianini@gmail.com")
                        url.set("http://www.danilopianini.org/")
                    }
                }
            }
        }
    }
}

pluginBundle {
    website = websiteUrl
    vcsUrl = websiteUrl
    tags = listOf("maven", "maven central", "ossrh", "central", "publish")
}

gradlePlugin {
    plugins {
        create("GradleLatex") {
            id = projectId
            displayName = fullName
            description = projectDetails
            implementationClass = pluginImplementationClass
        }
    }
}
if (System.getenv("CI") == true.toString()) {
    signing {
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
}
