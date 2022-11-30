group = "com.encodeering.luminous.system.partner"
version = "0.0.1-SNAPSHOT"

plugins {
    application
    kotlin ("jvm")
    kotlin ("plugin.serialization")
    id ("io.ktor.plugin") version "2.1.3"
}

repositories {
    mavenCentral ()
    maven ("https://repo1.maven.org/maven2")
}

dependencies {
    implementation (kotlin ("stdlib-jdk8"))
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-datetime-jvm:0.4.0")

    implementation ("ch.qos.logback:logback-classic:1.4.5")
    implementation ("io.ktor:ktor-server-core:2.1.3")
    implementation ("io.ktor:ktor-server-netty:2.1.3")
    implementation ("io.ktor:ktor-server-websockets:2.1.3")
    implementation ("io.ktor:ktor-server-metrics-micrometer:2.1.3")
    implementation ("io.micrometer:micrometer-registry-prometheus:1.10.1")
    implementation ("org.slf4j:slf4j-api:1.7.30")

    testImplementation ("io.ktor:ktor-client-websockets:2.1.3")
    testImplementation ("io.ktor:ktor-server-test-host:2.1.3")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation ("org.assertj:assertj-core:3.23.1")
    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testImplementation ("org.junit.jupiter:junit-jupiter-params:5.9.0")
    testRuntimeOnly    ("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

application {
    mainClass.set ("io.ktor.server.netty.EngineMain")
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
