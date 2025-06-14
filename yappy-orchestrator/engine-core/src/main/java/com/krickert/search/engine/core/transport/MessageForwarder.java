package com.krickert.search.engine.core.transport;

import com.krickert.search.engine.core.routing.RouteData;
import com.krickert.search.model.PipeStream;
import reactor.core.publisher.Mono;
import java.util.Optional;

/**
 * Interface for forwarding PipeStream messages to their next destination.
 * Implementations handle specific transport mechanisms (gRPC, Kafka, etc.).
 */
public interface MessageForwarder {
    
    /**
     * Forward a PipeStream message according to the routing information.
     * 
     * @param pipeStream The message to forward
     * @param routeData The routing information
     * @return Mono containing the processed PipeStream (if any) for continuing the pipeline
     */
    Mono<Optional<PipeStream>> forward(PipeStream pipeStream, RouteData routeData);
    
    /**
     * Check if this forwarder can handle the given transport type.
     * 
     * @param transportType The transport type from RouteData
     * @return true if this forwarder handles the transport type
     */
    boolean canHandle(RouteData.TransportType transportType);
    
    /**
     * Get the transport type this forwarder handles.
     * 
     * @return The transport type
     */
    RouteData.TransportType getTransportType();
}