package ru.job4j.pooh;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class QueueServiceTest {

    @Test
    public void whenPostThenGetQueue() {
        QueueService queueService = new QueueService();
        String paramForPostMethod = "temperature=18";
        queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod)
        );
        Resp result = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        assertThat(result.text()).isEqualTo("temperature=18");
    }

    @Test
    public void whenPostThenGetQueueTwice() {
        QueueService queueService = new QueueService();
        String paramForPostMethod1 = "temperature=18";
        String paramForPostMethod2 = "pressure=755";
        queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod1)
        );
        queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod2)
        );
        Resp temp = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        Resp pressure = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        assertThat(temp.text()).isEqualTo("temperature=18");
        assertThat(pressure.text()).isEqualTo("pressure=755");
    }

    @Test
    public void whenPostThenGetTwoDiffQueue() {
        QueueService queueService = new QueueService();
        String paramForPostMethod1 = "temperature=18";
        String paramForPostMethod2 = "HEX=4B0082";
        queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod1)
        );
        queueService.process(
                new Req("POST", "queue", "color", paramForPostMethod2)
        );
        Resp temp = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        Resp pressure = queueService.process(
                new Req("GET", "queue", "color", null)
        );
        assertThat(temp.text()).isEqualTo("temperature=18");
        assertThat(pressure.text()).isEqualTo("HEX=4B0082");
    }

    @Test
    public void whenPostThenGetTwoDiffQueueButOneQueueIsEmpty() {
        QueueService queueService = new QueueService();
        String paramForPostMethod2 = "HEX=4B0082";
        queueService.process(
                new Req("POST", "queue", "color", paramForPostMethod2)
        );
        Resp temp = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        Resp pressure = queueService.process(
                new Req("GET", "queue", "color", null)
        );
        assertThat(temp.text()).isEqualTo("");
        assertThat(pressure.text()).isEqualTo("HEX=4B0082");
    }

    @Test
    public void whenGetButEmptyQueue() {
        QueueService queueService = new QueueService();
        Resp temp = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        assertThat(temp.text()).isEqualTo("");
    }
}
