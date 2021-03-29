val name: String get () = settings.extra["quarkus.project.name"]!!.toString ()

rootProject.name = name

include  ("module:application")
project (":module:application").projectDir = file ("module/application")

pluginManagement {
    repositories {
        mavenLocal ()
        gradlePluginPortal ()
    }
    plugins {
        kotlin ("jvm") version "1.7.20"
    }
}
