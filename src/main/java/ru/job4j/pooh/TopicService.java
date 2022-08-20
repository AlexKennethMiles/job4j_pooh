package ru.job4j.pooh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicService implements Service {
    private ConcurrentHashMap<String,
            ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> map = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        Resp resp = null;
        if ("POST".equals(req.httpRequestType())) {
            ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> topic = map.putIfAbsent(
                    req.getSourceName(),
                    new ConcurrentHashMap<>()
            );
            if (topic == null) {
                topic = map.get(req.getSourceName());
            }
            topic.forEachValue(1, x -> x.add(req.getParam()));
            resp = new Resp("", "200 OK");
        } else if ("GET".equals(req.httpRequestType())) {
            ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> topic = map.putIfAbsent(req.getSourceName(),
                    new ConcurrentHashMap<>());
            if (topic == null) {
                topic = map.get(req.getSourceName());
            }
            ConcurrentLinkedQueue<String> queue = topic.putIfAbsent(req.getParam(), new ConcurrentLinkedQueue<>());
            resp = new Resp("", "200 OK");
            if (queue != null) {
                resp = new Resp(queue.poll(), "200 OK");
            }
        }
        return resp != null ? resp : new Resp("", "204 No Content");
    }
}
