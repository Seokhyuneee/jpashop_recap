package jpashop_recap.project1.service;

import jpashop_recap.project1.domain.*;
import jpashop_recap.project1.domain.item.Item;
import jpashop_recap.project1.repository.ItemRepository;
import jpashop_recap.project1.repository.MemberRepository;
import jpashop_recap.project1.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 1) 상품 주문 기능  2) 주문 내역 조회 기능
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 기능1-1 - 상품 주문 기능
     * 주문을 하기 위해서는
     * 1) Member Entity가 Item Entity에 존재하는 상품 중 하나를 선택한다.
     * 2) Member의 배송정보를 지정한다.
     * 3) 1과 2의 내용이 포함된 OrderItem Entity를 생성한다.
     * 4) 만들어진 모든 OrderItem Entity
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        Member member = memberRepository.findById(memberId).get();
        Item item = itemRepository.findOne(itemId);

        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        Order order = Order.createOrder(member, delivery, orderItem);

        orderRepository.save(order);
        return order.getId();
    }

    /**
     * 기능1-2 - 주문 취소 기능
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findOne(orderId);
        order.cancel();
    }

    /**
     * 기능2 - 주문 현황 조회
     */
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAll(orderSearch);
    }

}
