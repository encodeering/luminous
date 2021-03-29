group = "com.encodeering.luminous.application"

plugins {
    id ("io.quarkus") version ("1.12.2.Final")
    kotlin ("plugin.serialization")
}

dependencies {
    implementation ("io.quarkus:quarkus-arc")
    implementation ("io.quarkus:quarkus-vertx")
    implementation ("io.quarkus:quarkus-vertx-web")

    implementation (enforcedPlatform ("org.apache.camel:camel-bom:3.8.0"))
    implementation ("org.apache.camel:camel-ahc-ws")
    implementation ("org.apache.camel:camel-core")

    testImplementation ("org.apache.camel:camel-mock")
    testImplementation ("org.apache.camel:camel-test-junit5")
    testImplementation ("org.apache.camel:camel-testcontainers-junit5")
}
