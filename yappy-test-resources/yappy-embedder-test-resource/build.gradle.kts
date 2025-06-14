plugins {
    id("java-library")
    id("io.micronaut.test-resources")
    id("io.micronaut.library")
}

micronaut {
    version("4.8.2")
    testResources {
        enabled.set(true)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(platform(project(":bom")))
    annotationProcessor(platform(project(":bom")))
    
    // Base module test resource
    api(project(":yappy-test-resources:yappy-module-base-test-resource"))
    
    api("io.micronaut.testresources:micronaut-test-resources-core")
    api("io.micronaut.testresources:micronaut-test-resources-testcontainers")
    api("org.testcontainers:testcontainers")
    
    implementation("org.slf4j:slf4j-api")
    
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("io.micronaut:micronaut-jackson-databind")
    testRuntimeOnly(mn.logback.classic)
}

tasks.test {
    useJUnitPlatform()
}