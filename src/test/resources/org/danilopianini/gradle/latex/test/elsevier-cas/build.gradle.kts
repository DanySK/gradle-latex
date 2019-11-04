plugins {
  id("org.danilopianini.gradle-latex")
}
latex {
    "cas-sc-template" {
        bib = "cas-refs.bib"
    }
    "cas-dc-template" {
        bib = "cas-refs.bib"
    }
}
