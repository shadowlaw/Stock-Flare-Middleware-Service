package com.shadow.jse_middleware_service.util;

import org.springframework.data.domain.PageRequest;

public final class PageUtil {
    private PageUtil() {}

    public static PageRequest getPageRequest (Integer pageNumber, Integer pageSize, Integer defaultPageSize, Integer maxPageSize) {
        pageSize = pageSize > maxPageSize ? maxPageSize : pageSize;
        pageSize = pageSize < 1 ? defaultPageSize : pageSize;
        pageNumber = pageNumber < 0 ? 0 : pageNumber;

        return PageRequest.of(pageNumber, pageSize);

    }
}
