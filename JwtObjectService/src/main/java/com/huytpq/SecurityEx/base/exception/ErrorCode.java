package com.huytpq.SecurityEx.base.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements AbstractError {
    RECIPE_NOT_FOUND(1, "Không tìm thấy công thức món ăn. ", HttpStatus.NOT_FOUND),
    RECIPE_NAME_EXISTED(2, "Tên công thức món ăn đã tồn tại. ", HttpStatus.BAD_REQUEST),
    RECIPE_CATEGORY_NOT_FOUND(3, "Không tìm thấy danh mục món ăn. ", HttpStatus.NOT_FOUND),
    RECIPE_CATEGORY_NAME_EXISTED(4, "Tên danh mục món ăn đã tồn tại. ", HttpStatus.BAD_REQUEST),
    IMAGE_LIMIT_EXCEEDED(5, "Số lượng hình ảnh phải nhỏ hơn hoặc bằng 5.", HttpStatus.BAD_REQUEST),
    INVALID_INPUT(6, "Dữ liệu đầu vào là bắt buộc", HttpStatus.BAD_REQUEST),
    PAYLOAD_TOO_LARGE(7, "Vui lòng không upload Thumbnail file có dung lượng quá 10MB.", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_MEDIA_TYPE(8, "Thumbnail phải có định dạng ảnh.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(9, "Error processing file:", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_URL(10, "Ảnh không được để trống", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(11, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    POST_NOT_FOUND(12, "Không tìm thấy bài viết", HttpStatus.NOT_FOUND),
    POST_TITLE_EXISTED(13, "Tiêu đề bài viết đã tồn tại", HttpStatus.BAD_REQUEST),
    TAG_NOT_FOUND(14, "Không tìm thấy thẻ", HttpStatus.NOT_FOUND),
    TAG_NAME_EXISTED(15, "Tên thẻ đã tồn tại", HttpStatus.BAD_REQUEST),
    POST_TAG_NOT_FOUND(16, "Không tìm thấy liên kết giữa bài viết và thẻ", HttpStatus.NOT_FOUND),
    RECIPE_TAG_NOT_FOUND(17, "Không tìm thấy liên kết giữa công thức và thẻ", HttpStatus.NOT_FOUND),
    INGREDIENT_NOT_FOUND(18, "Không tìm thấy nguyên liệu", HttpStatus.NOT_FOUND),
    INGREDIENT_NAME_EXISTED(19, "Tên nguyên liệu đã tồn tại", HttpStatus.BAD_REQUEST),
    RECIPE_INGREDIENT_NOT_FOUND(20, "Không tìm thấy liên kết giữa công thức và nguyên liệu", HttpStatus.NOT_FOUND),
    FAVORITE_RECIPE_NOT_FOUND(23, "Không tìm thấy công thức yêu thích", HttpStatus.NOT_FOUND),
    USERNAME_EXISTED(24, "Tên người dùng đã tồn tại", HttpStatus.BAD_REQUEST);


    private final int code;

    private final String message;

    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}