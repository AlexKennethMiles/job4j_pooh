package ru.job4j.pooh;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class TopicServiceTest {

    @Test
    public void whenTopic() {
        TopicService topicService = new TopicService();
        String paramForPublisher = "temperature=18";
        String paramForSubscriber1 = "client407";
        String paramForSubscriber2 = "client6565";
        /* Режим topic. Подписываемся на топик weather. client407. */
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        /* Режим topic. Добавляем данные в топик weather. */
        topicService.process(
                new Req("POST", "topic", "weather", paramForPublisher)
        );
        /* Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client407. */
        Resp result1 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        /* Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client6565.
        Очередь отсутствует, т.к. еще не был подписан - получит пустую строку */
        Resp result2 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber2)
        );
        assertThat(result1.text()).isEqualTo("temperature=18");
        assertThat(result2.text()).isEqualTo("");
    }

    @Test
    public void whenTwoItemInQueueInTopic() {
        TopicService topicService = new TopicService();
        String paramForPublisher1 = "temperature=18";
        String paramForPublisher2 = "pressure=755";
        String paramForSubscriber1 = "client407";
        /* Режим topic. Подписываемся на топик weather. client407. */
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        /* Режим topic. Добавляем данные в топик weather. */
        topicService.process(
                new Req("POST", "topic", "weather", paramForPublisher1)
        );
        topicService.process(
                new Req("POST", "topic", "weather", paramForPublisher2)
        );
        /* Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client407. */
        Resp result1 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        assertThat(result1.text()).isEqualTo("temperature=18");
        /* Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client407. */
        Resp result2 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        assertThat(result2.text()).isEqualTo("pressure=755");
    }

    @Test
    public void whenTwoItemInQueueInTopicAndTwoSubscribers() {
        TopicService topicService = new TopicService();
        String paramForPublisher1 = "temperature=18";
        String paramForPublisher2 = "pressure=755";
        String paramForSubscriber1 = "client407";
        String paramForSubscriber2 = "client6565";
        /* Режим topic. Подписываемся на топик weather. client407. */
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        /* Режим topic. Подписываемся на топик weather. client6565. */
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber2)
        );
        /* Режим topic. Добавляем данные в топик weather. */
        topicService.process(
                new Req("POST", "topic", "weather", paramForPublisher1)
        );
        topicService.process(
                new Req("POST", "topic", "weather", paramForPublisher2)
        );
        /* Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client407. */
        Resp tempForFirst = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        /* Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client6565. */
        Resp tempForSec = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber2)
        );
        assertThat(tempForFirst.text()).isEqualTo("temperature=18");
        assertThat(tempForSec.text()).isEqualTo("temperature=18");
        /* Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client407. */
        Resp pressureForFirst = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        /* Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client6565. */
        Resp pressureForSec = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber2)
        );
        assertThat(pressureForFirst.text()).isEqualTo("pressure=755");
        assertThat(pressureForSec.text()).isEqualTo("pressure=755");
    }

    @Test
    public void whenTopicButNoSubscribe() {
        TopicService topicService = new TopicService();
        String paramForPublisher1 = "temperature=18";
        String paramForPublisher2 = "pressure=755";
        String paramForSubscriber1 = "client407";
        /* Режим topic. Добавляем данные в топик weather. */
        topicService.process(
                new Req("POST", "topic", "weather", paramForPublisher1)
        );
        topicService.process(
                new Req("POST", "topic", "weather", paramForPublisher2)
        );
        /* Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client407.
        Но так как клиент не подписан на топик, возвращается пустая строка. */
        Resp result1 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        assertThat(result1.text()).isEqualTo("");
        /* Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client6565.
        Клиент всё ещё не подписан на топик, но для него создалась пустая очередь, потому вместо строки
        получаем null */
        Resp result2 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        assertThat(result2.text()).isNull();
    }

    @Test
    public void whenDeleteButNotImplemented() {
        TopicService topicService = new TopicService();
        Resp temp = topicService.process(
                new Req("DELETE", "queue", "weather", null)
        );
        assertThat(temp.text()).isEqualTo("");
        assertThat(temp.status()).isEqualTo("501 Not Implemented");
    }
}
