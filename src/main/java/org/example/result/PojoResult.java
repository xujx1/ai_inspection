package org.example.result;

public class PojoResult<T> extends BaseResult<T> {

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
