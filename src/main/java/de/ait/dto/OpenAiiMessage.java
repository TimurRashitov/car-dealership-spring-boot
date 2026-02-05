package de.ait.dto;

public class OpenAiiMessage {
    String content;
    String role;

    public OpenAiiMessage(String content, String role) {
        this.content = content;
        this.role = role;
    }

}
