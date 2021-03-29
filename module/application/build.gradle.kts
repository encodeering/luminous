group = "com.encodeering.luminous.application"

plugins {
    id ("io.quarkus") version ("1.12.2.Final")
}

dependencies {
    implementation ("io.quarkus:quarkus-vertx")
    implementation ("io.quarkus:quarkus-vertx-web")
}
