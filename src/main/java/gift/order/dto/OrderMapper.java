package gift.order.dto;

import gift.order.entity.Order;

import java.time.LocalDateTime;

public class OrderMapper {
    public static OrderResponse toOrderResponse(Order order) {
        if (order == null) {
            return null;
        }

        return new OrderResponse(
            order.getId(),
            order.getOptionId(),
            order.getQuantity(),
            order.getOrderDateTime(),
            order.getMessage()
        );
    }

    public static Order toOrder(OrderRequest orderRequest) {
        if (orderRequest == null) {
            return null;
        }

        return new Order(
            orderRequest.optionId(),
            orderRequest.quantity(),
            LocalDateTime.now(),
            orderRequest.message()
        );
    }
}
