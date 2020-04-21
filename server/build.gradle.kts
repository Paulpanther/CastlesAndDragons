import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.3.71"
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

repositories {
    jcenter()
}

group = "com.paulmethfessel"
version = "0.1.0"

sourceSets.getByName("main") {
    java.srcDir("src/main/java")
    java.srcDir("src/main/kotlin")
}
sourceSets.getByName("test") {
    java.srcDir("src/test/java")
    java.srcDir("src/test/kotlin")
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))

    implementation("org.java-websocket:Java-WebSocket:1.4.1")
    implementation("org.slf4j:slf4j-simple:1.7.25")
    implementation("com.xenomachina:kotlin-argparser:2.0.7")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.0.2")
}

tasks.withType<ShadowJar>() {
    manifest {
        attributes["Main-Class"] = "com.paulmethfessel.cad.CastlesAndDragonsKt"
    }
}
