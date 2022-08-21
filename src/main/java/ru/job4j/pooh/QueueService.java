package ru.job4j.pooh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService implements Service {
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> map = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        if ("POST".equals(req.httpRequestType())) {
            map.putIfAbsent(req.getSourceName(), new ConcurrentLinkedQueue<>());
            map.get(req.getSourceName()).add(req.getParam());
            return new Resp("", "200 OK");
        } else if ("GET".equals(req.httpRequestType())) {
            ConcurrentLinkedQueue<String> queue = map.getOrDefault(req.getSourceName(), new ConcurrentLinkedQueue<>());
            return new Resp(
                    queue.isEmpty() ? "" : queue.poll(),
                    "200 OK"
            );
        }
        return new Resp("", "501 Not Implemented");
    }

}
