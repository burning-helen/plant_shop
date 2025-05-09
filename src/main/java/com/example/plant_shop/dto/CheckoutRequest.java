package com.example.plant_shop.dto;

import com.example.plant_shop.model.OrderForm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {
    private OrderForm orderForm;
    private List<Long> selectedItemIds;

    public OrderForm getOrderForm() {
        return orderForm;
    }

    public void setOrderForm(OrderForm orderForm) {
        this.orderForm = orderForm;
    }

    public List<Long> getSelectedItemIds() {
        return selectedItemIds;
    }

    public void setSelectedItemIds(List<Long> selectedItemIds) {
        this.selectedItemIds = selectedItemIds;
    }
}

