package io.vertx.axle.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import io.vertx.axle.core.Vertx;
import io.vertx.core.json.JsonObject;

public class RedisClientTest {

    @Rule
    public GenericContainer container = new GenericContainer("redis")
            .withExposedPorts(6379);

    private Vertx vertx;

    @Before
    public void setUp() {
        vertx = Vertx.vertx();
        assertThat(vertx).isNotNull();
    }

    @After
    public void tearDown() {
        vertx.close();
    }

    @Test
    public void testAxleAPI() {
        RedisClient client = RedisClient.create(vertx, new JsonObject()
                .put("port", container.getMappedPort(6379))
                .put("host", container.getContainerIpAddress()));

        JsonObject object = client.hset("book", "title", "The Hobbit")
                .thenCompose(x -> client.hgetall("book"))
                .toCompletableFuture()
                .join();
        assertThat(object).contains(entry("title", "The Hobbit"));
    }
}
