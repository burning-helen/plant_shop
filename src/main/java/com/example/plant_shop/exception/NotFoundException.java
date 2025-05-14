package com.example.plant_shop.exception;

/**
 * Исключение, выбрасываемое, когда запрашиваемый ресурс не найден.
 *
 * Это unchecked-исключение (наследуется от {@link RuntimeException}), которое может использоваться
 * для сигнализации о том, что объект (например, пользователь, растение, заказ и т.д.) не был найден в базе данных.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Создает новое исключение с указанным сообщением.
     *
     * @param message подробное сообщение об ошибке
     */
    public NotFoundException(String message) {
        super(message);
    }
}
