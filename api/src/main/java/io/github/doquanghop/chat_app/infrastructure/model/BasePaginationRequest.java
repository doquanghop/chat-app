package io.github.doquanghop.chat_app.infrastructure.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class BasePaginationRequest {
    private String sortBy = "createdAt";
    private int page = 0;
    private int pageSize = 10;
}
