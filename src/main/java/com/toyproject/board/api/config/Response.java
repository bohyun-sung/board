package com.toyproject.board.api.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Response <T>{

    private final boolean result = true;
    private T data;

    public static Response<Void> success() {
        return new Response<>(null);
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(data);
    }

    public String toStream() {
        if(data == null){
            return "{" +
                    "\"result\":" + "\"" + result + "\"," +
                    "\"data\":" +  null +  "}";
        } else {
            return "{" +
                    "\"result\":" + "\"" + result + "\"," +
                    "\"data\":" + "\"" + data + "\"" + "}";
        }
    }
}
