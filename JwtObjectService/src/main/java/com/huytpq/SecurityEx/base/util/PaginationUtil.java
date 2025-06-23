package com.huytpq.SecurityEx.base.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PaginationUtil {

    // Mặc định nếu không truyền page hoặc limit
    private static final int DEFAULT_PAGE = 1; // Trang mặc định là 1 cho người dùng
    private static final int DEFAULT_LIMIT = 10;
    private static final String DEFAULT_SORT_FIELD = "id";
    private static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;

    /**
     * Tạo PageRequest với các tham số phân trang
     *
     * @param page Trang (default: 1)
     * @param limit Số item mỗi trang (default: 10)
     * @param sortField Trường để sắp xếp (default: "id")
     * @param sortDirection Hướng sắp xếp (default: ASC)
     * @return PageRequest
     */
    public static PageRequest createPageRequest(Integer page, Integer limit, String sortField, Sort.Direction sortDirection) {
        // Gán giá trị mặc định nếu page hoặc limit không được truyền
        int pageNumber = (page != null && page >= 1) ? page - 1 : DEFAULT_PAGE - 1; // Giảm 1 để khớp với Spring Data (page 0)
        int pageSize = (limit != null && limit > 0) ? limit : DEFAULT_LIMIT;
        String field = (sortField != null && !sortField.isEmpty()) ? sortField : DEFAULT_SORT_FIELD;
        Sort.Direction direction = (sortDirection != null) ? sortDirection : DEFAULT_SORT_DIRECTION;

        return PageRequest.of(pageNumber, pageSize, Sort.by(direction, field));
    }

    /**
     * Tạo PageRequest với sort mặc định (ASC, theo id)
     *
     * @param page Trang
     * @param limit Số item mỗi trang
     * @return PageRequest
     */
    public static PageRequest createPageRequest(Integer page, Integer limit) {
        return createPageRequest(page, limit, DEFAULT_SORT_FIELD, DEFAULT_SORT_DIRECTION);
    }
}