package org.example.result;

import java.io.Serial;

public class PojoResult<T> extends BaseResult<T> {
    @Serial
    private static final long serialVersionUID = 3407218227735447235L;

    @Override
    public T getContent() {
        return super.content;
    }

    @Override
    public void setContent(T content) {
        super.content = content;
    }
}
