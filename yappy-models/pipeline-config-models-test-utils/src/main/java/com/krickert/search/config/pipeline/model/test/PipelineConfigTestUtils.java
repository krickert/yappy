package com.krickert.search.config.pipeline.model.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.krickert.search.config.pipeline.model.*; // Import all from model
import com.krickert.search.config.pipeline.model.SchemaReference;

import java.io.IOException;
import java.util.Collections; // Import Collections
import java.util.List; // Import List
import java.util.Map;
import java.util.Set; // Import Set
import java.util.function.Function;

/**
 * Utility class for testing pipeline configuration models.
 * Provides methods for serialization, deserialization, and creation of model objects.
 */
public class PipelineConfigTestUtils {

    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    /**
     * Creates an ObjectMapper configured for pipeline configuration models.
     */
    public static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper;
    }

    /**
     * Serializes a pipeline configuration object to JSON.
     *
     * @param object The object to serialize
     * @return The JSON string
     * @throws JsonProcessingException If serialization fails
     */
    public static String toJson(Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    /**
     * Deserializes a JSON string to a pipeline configuration object.
     *
     * @param json      The JSON string
     * @param valueType The class of the object to deserialize to
     * @param <T>       The type of the object
     * @return The deserialized object
     * @throws IOException If deserialization fails
     */
    public static <T> T fromJson(String json, Class<T> valueType) throws IOException {
        return OBJECT_MAPPER.readValue(json, valueType);
    }

    /**
     * Performs a round-trip serialization and deserialization of an object.
     * This is useful for testing that an object can be correctly serialized and deserialized.
     *
     * @param object    The object to serialize and deserialize
     * @param valueType The class of the object
     * @param <T>       The type of the object
     * @return The deserialized object
     * @throws IOException If serialization or deserialization fails
     */
    public static <T> T roundTrip(T object, Class<T> valueType) throws IOException {
        String json = toJson(object);
        return fromJson(json, valueType);
    }

    /**
     * Creates a JsonConfigOptions object from a JSON string.
     *
     * @param jsonString The JSON string
     * @return The JsonConfigOptions object
     * @throws JsonProcessingException If parsing the JSON fails
     */
    public static PipelineStepConfig.JsonConfigOptions createJsonConfigOptions(String jsonString) throws JsonProcessingException {
        if (jsonString == null || jsonString.isBlank()) {
            // Return an empty JsonNode if the string is null or blank,
            // as JsonConfigOptions expects a JsonNode.
            return new PipelineStepConfig.JsonConfigOptions(OBJECT_MAPPER.createObjectNode(), Map.of());
        }
        return new PipelineStepConfig.JsonConfigOptions(OBJECT_MAPPER.readTree(jsonString), Map.of());
    }

    /**
     * Creates a JsonConfigOptions object from a JsonNode.
     *
     * @param jsonNode The JsonNode
     * @return The JsonConfigOptions object
     */
    public static PipelineStepConfig.JsonConfigOptions createJsonConfigOptions(JsonNode jsonNode) {
        return new PipelineStepConfig.JsonConfigOptions(jsonNode, Map.of());
    }

    /**
     * Creates a JsonConfigOptions object from a map of configuration parameters.
     *
     * @param configParams The configuration parameters
     * @return The JsonConfigOptions object
     */
    public static PipelineStepConfig.JsonConfigOptions createJsonConfigOptions(Map<String, String> configParams) {
        // Assuming if only configParams are provided, jsonConfig can be null or an empty node.
        // Let's default to an empty JsonNode for consistency.
        return new PipelineStepConfig.JsonConfigOptions(OBJECT_MAPPER.createObjectNode(), configParams);
    }

    /**
     * Creates a ProcessorInfo object for a gRPC service.
     *
     * @param grpcServiceName The name of the gRPC service
     * @return The ProcessorInfo object
     */
    public static PipelineStepConfig.ProcessorInfo createGrpcProcessorInfo(String grpcServiceName) {
        return new PipelineStepConfig.ProcessorInfo(grpcServiceName, null);
    }

    /**
     * Creates a ProcessorInfo object for an internal processor.
     *
     * @param internalProcessorBeanName The name of the internal processor bean
     * @return The ProcessorInfo object
     */
    public static PipelineStepConfig.ProcessorInfo createInternalProcessorInfo(String internalProcessorBeanName) {
        return new PipelineStepConfig.ProcessorInfo(null, internalProcessorBeanName);
    }

    /**
     * Creates an OutputTarget object for a Kafka transport.
     *
     * @param topic The Kafka topic
     * @param targetStepName The name of the target step for this output
     * @return The OutputTarget object
     */
    public static PipelineStepConfig.OutputTarget createKafkaOutputTarget(String topic, String targetStepName) {
        return createKafkaOutputTarget(topic, targetStepName, Collections.emptyMap());
    }

    /**
     * Creates an OutputTarget object for a Kafka transport.
     *
     * @param topic                   The Kafka topic
     * @param targetStepName          The name of the target step for this output
     * @param kafkaProducerProperties The Kafka producer properties
     * @return The OutputTarget object
     */
    public static PipelineStepConfig.OutputTarget createKafkaOutputTarget(
            String topic, String targetStepName, Map<String, String> kafkaProducerProperties) {
        KafkaTransportConfig kafkaTransport = new KafkaTransportConfig(topic, kafkaProducerProperties);
        return new PipelineStepConfig.OutputTarget(targetStepName, TransportType.KAFKA, null, kafkaTransport);
    }

    /**
     * Creates an OutputTarget object for a gRPC transport.
     *
     * @param serviceName          The gRPC service name
     * @param targetStepName       The name of the target step for this output
     * @return The OutputTarget object
     */
    public static PipelineStepConfig.OutputTarget createGrpcOutputTarget(String serviceName, String targetStepName) {
        return createGrpcOutputTarget(serviceName, targetStepName, Collections.emptyMap());
    }


    /**
     * Creates an OutputTarget object for a gRPC transport.
     *
     * @param serviceName          The gRPC service name
     * @param targetStepName       The name of the target step for this output
     * @param grpcClientProperties The gRPC client properties
     * @return The OutputTarget object
     */
    public static PipelineStepConfig.OutputTarget createGrpcOutputTarget(
            String serviceName, String targetStepName, Map<String, String> grpcClientProperties) {
        GrpcTransportConfig grpcTransport = new GrpcTransportConfig(serviceName, grpcClientProperties);
        return new PipelineStepConfig.OutputTarget(targetStepName, TransportType.GRPC, grpcTransport, null);
    }

    /**
     * Creates an OutputTarget object for an internal transport.
     *
     * @param targetStepName The name of the target step
     * @return The OutputTarget object
     */
    public static PipelineStepConfig.OutputTarget createInternalOutputTarget(String targetStepName) {
        return new PipelineStepConfig.OutputTarget(targetStepName, TransportType.INTERNAL, null, null);
    }

    /**
     * Creates a KafkaInputDefinition.
     *
     * @param listenTopic The topic to listen to.
     * @return The KafkaInputDefinition object.
     */
    public static KafkaInputDefinition createKafkaInput(String listenTopic) {
        return createKafkaInput(List.of(listenTopic), listenTopic + "-group", Collections.emptyMap());
    }

    /**
     * Creates a KafkaInputDefinition.
     *
     * @param listenTopics            List of topics to listen to.
     * @param consumerGroupId         The consumer group ID.
     * @param kafkaConsumerProperties Additional Kafka consumer properties.
     * @return The KafkaInputDefinition object.
     */
    public static KafkaInputDefinition createKafkaInput(List<String> listenTopics, String consumerGroupId, Map<String, String> kafkaConsumerProperties) {
        return KafkaInputDefinition.builder()
                .listenTopics(listenTopics)
                .consumerGroupId(consumerGroupId)
                .kafkaConsumerProperties(kafkaConsumerProperties)
                .build();
    }

    /**
     * Creates a basic PipelineStepConfig.
     *
     * @param stepName        The name of the step.
     * @param stepType        The type of the step.
     * @param processorImplId The implementation ID for the processor (can be gRPC service name or internal bean name).
     * @param customConfig    The custom configuration for the step.
     * @param kafkaInputs     List of Kafka inputs.
     * @param outputs         Map of output targets.
     * @return The PipelineStepConfig object.
     */
    public static PipelineStepConfig createStep(
            String stepName,
            StepType stepType,
            String processorImplId,
            PipelineStepConfig.JsonConfigOptions customConfig,
            List<KafkaInputDefinition> kafkaInputs,
            Map<String, PipelineStepConfig.OutputTarget> outputs) {

        PipelineStepConfig.ProcessorInfo processorInfo = null;
        if (processorImplId != null && !processorImplId.isBlank()) {
            // Heuristic: if it looks like a bean name (e.g., contains "Bean" or is camelCase without dots),
            // assume internal. Otherwise, assume gRPC. This might need refinement based on your conventions.
            if (processorImplId.contains(".") || processorImplId.contains("/")) { // Common in gRPC full service names
                processorInfo = createGrpcProcessorInfo(processorImplId);
            } else {
                processorInfo = createInternalProcessorInfo(processorImplId);
            }
        }

        return PipelineStepConfig.builder()
                .stepName(stepName)
                .stepType(stepType)
                .processorInfo(processorInfo) // Can be null if step doesn't have a processor
                .customConfig(customConfig)
                .kafkaInputs(kafkaInputs == null ? Collections.emptyList() : kafkaInputs)
                .outputs(outputs == null ? Collections.emptyMap() : outputs)
                .build();
    }


    /**
     * Creates a PipelineModuleConfiguration.
     *
     * @param name            The display name of the module.
     * @param implementationId The unique implementation ID.
     * @param schemaRef       Optional schema reference for the module's own configuration (if any)
     *                        or for steps using this module if they don't define their own.
     * @param customConfig    Custom configuration for the module itself.
     * @return The PipelineModuleConfiguration object.
     */
    public static PipelineModuleConfiguration createModule(String name, String implementationId, SchemaReference schemaRef, Map<String, Object> customConfig) {
        return new PipelineModuleConfiguration(name, implementationId, schemaRef, customConfig);
    }

    /**
     * Creates a PipelineModuleConfiguration with a schema reference.
     *
     * @param name            The display name of the module.
     * @param implementationId The unique implementation ID.
     * @param schemaRef       The schema reference for steps using this module.
     * @param moduleCustomConfig A map representing the module's own custom configuration.
     * @return The PipelineModuleConfiguration object.
     */
    public static PipelineModuleConfiguration createModuleWithSchema(String name, String implementationId, SchemaReference schemaRef, Map<String, Object> moduleCustomConfig) {
        return new PipelineModuleConfiguration(name, implementationId, schemaRef, moduleCustomConfig);
    }


    /**
     * Creates a PipelineConfig.
     *
     * @param name  The name of the pipeline.
     * @param steps A map of step names to their configurations.
     * @return The PipelineConfig object.
     */
    public static PipelineConfig createPipeline(String name, Map<String, PipelineStepConfig> steps) {
        return new PipelineConfig(name, steps);
    }


    /**
     * Performs a serialization test on a pipeline configuration object.
     * Serializes the object to JSON, deserializes it back, and compares the result with the original.
     *
     * @param object    The object to test
     * @param valueType The class of the object
     * @param <T>       The type of the object
     * @return True if the test passes, false otherwise
     */
    public static <T> boolean testSerialization(T object, Class<T> valueType) {
        try {
            T roundTripped = roundTrip(object, valueType);
            return object.equals(roundTripped);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Performs a serialization test on a pipeline configuration object with a custom equality check.
     * Serializes the object to JSON, deserializes it back, and compares the result with the original using the provided equality function.
     *
     * @param object           The object to test
     * @param valueType        The class of the object
     * @param equalityFunction A function that compares two objects for equality
     * @param <T>              The type of the object
     * @return True if the test passes, false otherwise
     */
    public static <T> boolean testSerialization(T object, Class<T> valueType, Function<T, Boolean> equalityFunction) {
        try {
            T roundTripped = roundTrip(object, valueType);
            return equalityFunction.apply(roundTripped);
        } catch (IOException e) {
            return false;
        }
    }
}