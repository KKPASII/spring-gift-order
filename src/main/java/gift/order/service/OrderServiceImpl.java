package gift.order.service;

import gift.member.entity.Member;
import gift.order.repository.OrderRepository;
import gift.order.dto.OrderMapper;
import gift.order.dto.OrderRequest;
import gift.order.dto.OrderResponse;
import gift.order.entity.Order;
import gift.product.entity.Option;
import gift.product.repository.OptionRepository;
import gift.wish.entity.Wish;
import gift.wish.repository.WishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    private final OptionRepository optionRepository;
    private final OrderRepository orderRepository;
    private final WishRepository wishRepository;

    OrderServiceImpl(OptionRepository optionRepository, OrderRepository orderRepository, WishRepository wishRepository) {
        this.optionRepository = optionRepository;
        this.orderRepository = orderRepository;
        this.wishRepository = wishRepository;
    }

    @Override
    @Transactional
    public OrderResponse doOrder(Member member, OrderRequest orderRequest) {
        Option option = optionRepository.findById(orderRequest.optionId())
            .orElseThrow(() -> new IllegalArgumentException("해당 옵션이 존재하지 않습니다"));
        option.subtractQuantity(orderRequest.quantity());

        Order newOrder = OrderMapper.toOrder(orderRequest);
        Order savedOrder = orderRepository.save(newOrder);

        Optional<Wish> wish = wishRepository.findByMemberIdAndProductId(member.getId(), option.getProduct().getId());
        wishRepository.deleteById(wish.get().getId());
        return OrderMapper.toOrderResponse(savedOrder);
    }
}
