plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.nikitahohulia.cqrses"
version = "0.0.1-SNAPSHOT"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

repositories {
    mavenCentral()
    maven { url = uri("https://repo.akka.io/maven") }   // офіційний репо Akka 2.7+
}

// — Version
val akkaVersion = "2.8.5"   // ядро Akka (BSL 1.1)
val akkaProjVersion = "1.5.4"  // Akka Projections (сумісні з 2.9)
val alpakkaKafkaVersion = "7.0.3"   // Alpakka Kafka (сумісний з 2.9)
val cassandraVersion = "1.1.0"     // Akka Persistence Cassandra
val reactorBomVersion = "2024.0.7"  // Project Reactor BOM (reactor-core 3.7.7)

dependencies {
    // BOM (enforcedPlatform блокує сторонні версії)
    implementation(enforcedPlatform("com.typesafe.akka:akka-bom_2.13:$akkaVersion"))

    // - Project Reactor BOM
    implementation(enforcedPlatform("io.projectreactor:reactor-bom:$reactorBomVersion"))

    // — core Akka та streams
    implementation("com.typesafe.akka:akka-actor-typed_2.13")
    implementation("com.typesafe.akka:akka-stream_2.13")
    implementation("com.typesafe.akka:akka-serialization-jackson_2.13")

    // — Event Sourcing
    implementation("com.typesafe.akka:akka-persistence-typed_2.13")
    implementation("com.typesafe.akka:akka-persistence-query_2.13")
    implementation("com.typesafe.akka:akka-persistence-cassandra_2.13:$cassandraVersion")

    // — Projections (read-side)
    implementation("com.lightbend.akka:akka-projection-eventsourced_2.13:$akkaProjVersion")
    implementation("com.lightbend.akka:akka-projection-cassandra_2.13:$akkaProjVersion")
    implementation("com.lightbend.akka:akka-projection-kafka_2.13:$akkaProjVersion")

    // — Kafka stream connector
    implementation("com.typesafe.akka:akka-stream-kafka_2.13:$alpakkaKafkaVersion")

    // — Spring WebFlux REST-ендпоінт
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // — Kotlin/Jackson утиліти
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // — Reactor + Coroutines
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // — Тестові залежності
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")

    // Akka test-kit’и
    testImplementation("com.typesafe.akka:akka-actor-testkit-typed_2.13")
    testImplementation("com.lightbend.akka:akka-projection-testkit_2.13:$akkaProjVersion")
    testImplementation("com.typesafe.akka:akka-stream-kafka-testkit_2.13:$alpakkaKafkaVersion")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}