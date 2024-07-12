package pl.sknikod.kodemygateway.infrastructure.module.oauth2.util.route;

import org.springframework.cloud.gateway.config.GlobalCorsProperties;
import org.springframework.cloud.gateway.handler.FilteringWebHandler;
import org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.core.env.Environment;

public class GatewayRouteHandlerMapping extends RoutePredicateHandlerMapping {
    public GatewayRouteHandlerMapping(
            FilteringWebHandler webHandler, RouteLocator routeLocator,
            GlobalCorsProperties globalCorsProperties, Environment environment
    ) {
        super(webHandler, routeLocator, globalCorsProperties, environment);
    }
}
