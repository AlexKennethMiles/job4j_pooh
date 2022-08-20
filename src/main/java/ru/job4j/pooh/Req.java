package ru.job4j.pooh;

public class Req {
    private final String httpRequestType;
    private final String poohMode;
    private final String sourceName;
    private final String param;

    public Req(String httpRequestType, String poohMode, String sourceName, String param) {
        this.httpRequestType = httpRequestType;
        this.poohMode = poohMode;
        this.sourceName = sourceName;
        this.param = param;
    }

    public static Req of(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException();
        }
        String[] buff = content.split(System.lineSeparator());
        String httpRequestType = buff[0].contains("GET") ? "GET" : buff[0].contains("POST") ? "POST" : "";
        String poohMode = buff[0].contains("queue") ? "queue" : buff[0].contains("topic") ? "topic" : "";
        String sourceName = buff[0].substring(buff[0].indexOf('/') + poohMode.length() + 2, buff[0].lastIndexOf(" "));
        if (sourceName.contains("/")) {
            sourceName = sourceName.substring(0, sourceName.indexOf("/"));
        }
        String param = buff.length == 8 ? buff[7]
                : buff[0].contains("/topic/" + sourceName + "/") ? buff[0].substring(
                buff[0].lastIndexOf(sourceName) + sourceName.length() + 1, buff[0].lastIndexOf(" ")) : "";
        return new Req(httpRequestType, poohMode, sourceName, param);
    }

    public String httpRequestType() {
        return httpRequestType;
    }

    public String getPoohMode() {
        return poohMode;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getParam() {
        return param;
    }
}
