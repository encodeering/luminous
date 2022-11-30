rootProject.name = "partner"

pluginManagement {
    repositories {
        mavenLocal ()
        gradlePluginPortal ()
    }
    plugins {
        kotlin ("jvm")                  version "1.7.20"
        kotlin ("plugin.serialization") version "1.7.20"
    }
}
