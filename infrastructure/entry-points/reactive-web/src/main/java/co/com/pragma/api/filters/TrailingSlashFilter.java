package co.com.pragma.api.filters;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class TrailingSlashFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        System.out.println("Entro al filter para quitar el / -> path: " + path); // TODO delete this

        if (path.endsWith("/") && path.length() > 1) {
            String newPath = path.substring(0, path.length() - 1);
            URI newUri = UriComponentsBuilder.fromUri(exchange.getRequest().getURI())
                    .replacePath(newPath)
                    .build(true)
                    .toUri();

            // Cacheamos el body del request para poder reusarlo
            Flux<DataBuffer> cachedBody = exchange.getRequest().getBody().cache();

            ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest().mutate().uri(newUri).build()) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return cachedBody;
                }
            };

            System.out.println("retornando la nueva url " + newUri);// TODO delete this
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        }

        return chain.filter(exchange);
    }
}
