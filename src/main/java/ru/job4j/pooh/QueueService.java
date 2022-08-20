package ru.job4j.pooh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService implements Service {
    private ConcurrentHashMap<String, ConcurrentLinkedQueue> map = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        Resp resp = null;
        if ("POST".equals(req.httpRequestType())) {
            ConcurrentLinkedQueue<String> queue = map.putIfAbsent(req.getSourceName(), new ConcurrentLinkedQueue());
            if (queue == null) {
                queue = map.get(req.getSourceName());
            }
            queue.add(req.getParam());
            resp = new Resp("", "200 OK");
        } else if ("GET".equals(req.httpRequestType())) {
            ConcurrentLinkedQueue<String> queue = map.get(req.getSourceName());
            if (queue != null) {
                resp = new Resp(queue.poll(), "200 OK");
            }
        }
        return resp != null ? resp : new Resp("", "204 No Content");
    }
}
