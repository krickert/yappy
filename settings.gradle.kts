// settings.gradle.kts
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("mn") {
            from("io.micronaut.platform:micronaut-platform:4.8.2")
        }
    }
}

rootProject.name = "yappy-platform-build"

// Include all the subprojects
// Build order:
// 1. Core dependencies and test resources
// 2. Consul config
// 3. Engine
// 4. Modules
// 5. Containers
// 6. Integration tests

include(
    // Core dependencies
    "bom",
    "util",
    "yappy-kafka-slot-manager",

    // Test resources (needed by consul-config and engine)
    "yappy-test-resources",
    "yappy-test-resources:consul-test-resource",
    "yappy-test-resources:apicurio-test-resource",
    "yappy-test-resources:moto-test-resource",
    "yappy-test-resources:apache-kafka-test-resource",
    "yappy-test-resources:opensearch3-test-resource",
    "yappy-test-resources:yappy-module-base-test-resource",
    "yappy-test-resources:yappy-chunker-test-resource",
    "yappy-test-resources:yappy-tika-test-resource",
    "yappy-test-resources:yappy-embedder-test-resource",
    "yappy-test-resources:yappy-echo-test-resource",
    "yappy-test-resources:yappy-test-module-test-resource",
    "yappy-test-resources:yappy-engine-test-resource",

    // Models
    "yappy-models",
    "yappy-models:pipeline-config-models",
    "yappy-models:pipeline-config-models-test-utils",
    "yappy-models:protobuf-models",
    "yappy-models:protobuf-models-test-data-resources",

    // Consul config
    "yappy-consul-config",

    // Engine (no longer depends on modules)
    // "yappy-engine", // DISABLED - replaced by yappy-orchestrator
    
    // New orchestrator - clean architecture
    "yappy-orchestrator",
    "yappy-orchestrator:engine-core",
    "yappy-orchestrator:engine-integration-test",

    // Modules - needed for container builds
    "yappy-modules:tika-parser",
    "yappy-modules:chunker",
    "yappy-modules:embedder",

    // TODO: Add these back after engine is updated
    "yappy-modules:echo",
    "yappy-modules:test-module",
    // "yappy-modules:s3-connector",
    // "yappy-modules:opensearch-sink",
    // "yappy-modules:yappy-connector-test-server"
    
    // Containers
    "containers:chunker",
    "containers:echo",

    // Integration tests
    "yappy-integration-test",
    
    // Module Registration (renamed from yappy-registration-cli)
    "yappy-module-registration"
)
