package gift.order.service;

import gift.member.entity.Member;
import gift.order.dto.OrderRequest;
import gift.order.dto.OrderResponse;

public interface OrderService {
    OrderResponse doOrder(Member member, OrderRequest orderRequest);
}
