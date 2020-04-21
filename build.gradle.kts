import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `java`
    `maven-publish`
    `signing`
    id("org.danilopianini.git-sensitive-semantic-versioning")
    kotlin("jvm")
    id("com.gradle.plugin-publish")
    id("org.danilopianini.publish-on-central")
    id("org.jetbrains.dokka")
    id("io.gitlab.arturbosch.detekt")
    id("org.jlleitschuh.gradle.ktlint")
}

gitSemVer {
    version = computeGitSemVer()
}

group = "org.danilopianini"
val projectId = "$group.$name"
val fullName = "Gradle Latex Plugin"
val websiteUrl = "https://github.com/DanySK/gradle-latex"
val projectDetails = "A plugin for compiling LaTeX, inspired by https://github.com/csabasulyok/gradle-latex"
val pluginImplementationClass = "org.danilopianini.gradle.latex.Latex"

repositories {
    mavenCentral()
    maven {
        url = uri("https://dl.bintray.com/kotlin/kotlinx.html/")
        content {
            includeGroup("org.jetbrains.kotlinx")
        }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    testImplementation(gradleTestKit())
    testImplementation("io.github.classgraph:classgraph:_")
    testImplementation("com.uchuhimo:konf-yaml:_")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:_")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
}

tasks.withType<DokkaTask> {
    outputDirectory = "$buildDir/javadoc"
    outputFormat = "javadoc"
}

publishOnCentral {
    projectDescription.set(projectDetails)
    projectLongName.set(fullName)
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

detekt {
    failFast = true
    buildUponDefaultConfig = true
    config = files("$projectDir/config/detekt.yml")
    reports {
        html.enabled = true // observe findings in your browser with structure and code snippets
        xml.enabled = true // checkstyle like format mainly for integrations like Jenkins
        txt.enabled = true // similar to the console output, contains issue signature to manually edit baseline files
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
