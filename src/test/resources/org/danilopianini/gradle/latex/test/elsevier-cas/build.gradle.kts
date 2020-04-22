plugins {
  id("org.danilopianini.gradle-latex")
}
latex {
    "cas-sc-template"() {
        watching = listOf("figs", "cas-refs.bib")
    }
    "cas-dc-template"()
}
