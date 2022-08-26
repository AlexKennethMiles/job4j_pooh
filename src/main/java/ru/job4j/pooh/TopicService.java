package ru.job4j.pooh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TopicService implements Service {
    private final ConcurrentHashMap<String,
            ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> map = new ConcurrentHashMap<>();

    @Override
    public Resp process(Req req) {
        Resp resp = new Resp("", "501 Not Implemented");
        if ("POST".equals(req.httpRequestType())) {
            ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> topic = map.get(req.getSourceName());
            if (topic != null) {
                map.get(req.getSourceName()).forEachValue(1, x -> x.add(req.getParam()));
                resp = new Resp("", "200 OK");
            }
        } else if ("GET".equals(req.httpRequestType())) {
            map.putIfAbsent(req.getSourceName(), new ConcurrentHashMap<>());
            map.get(req.getSourceName()).putIfAbsent(req.getParam(), new ConcurrentLinkedQueue<>());
            String getString = map.get(req.getSourceName()).get(req.getParam()).poll();
            if (getString != null) {
                resp = new Resp(getString, "200 OK");
            } else {
                resp = new Resp("", "200 OK");
            }
        }
        return resp;
    }
}
