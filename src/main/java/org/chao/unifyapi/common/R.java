package org.chao.unifyapi.common;

import lombok.Data;

@Data
public class R<T> {
    private String code;
    private String message;
    private T data;
}
