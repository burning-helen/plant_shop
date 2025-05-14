package com.example.plant_shop.model;

/**
 * Вспомогательный класс формы заказа, содержащий адрес доставки и способ оплаты.
 * Используется для передачи данных из формы оформления заказа.
 */
public class OrderForm {

    /**
     * Адрес доставки, введённый пользователем.
     */
    private String deliveryAddress;

    /**
     * Выбранный пользователем способ оплаты.
     */
    private String paymentMethod;

    /**
     * Получить адрес доставки.
     * @return строка с адресом доставки
     */
    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    /**
     * Установить адрес доставки.
     * @param deliveryAddress строка с адресом
     */
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    /**
     * Получить способ оплаты.
     * @return строка с методом оплаты
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Установить способ оплаты.
     * @param paymentMethod строка с методом оплаты
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
