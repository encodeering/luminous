group = "com.encodeering.luminous"

plugins {
    java
    kotlin ("jvm")
}

allprojects {
    version = "0.0.1-SNAPSHOT" // git sha[8] should be used later

    repositories {
        mavenCentral ()
        maven ("https://repo1.maven.org/maven2")
    }
}

configure (subprojects.filter { it.name != "module" }) {
    apply (plugin = "org.gradle.java")
    apply (plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        implementation (kotlin ("stdlib-jdk8"))
        implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
        implementation ("org.slf4j:slf4j-api:1.7.30")

        implementation (enforcedPlatform ("io.quarkus:quarkus-bom:1.12.2.Final"))

        testImplementation ("org.mockito:mockito-core:4.0.0")
        testImplementation ("org.assertj:assertj-core:3.23.1")
        testImplementation ("org.junit.jupiter:junit-jupiter-api:5.7.0")
        testImplementation ("org.junit.jupiter:junit-jupiter-params:5.9.0")
        testRuntimeOnly    ("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    }

    tasks {
        compileKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }

        compileTestKotlin {
            kotlinOptions.jvmTarget = "1.8"
        }

        test {
            useJUnitPlatform {
                includeEngines ("junit-jupiter")
            }

            testLogging {
                events ("FAILED")
            }
        }
    }
}
