package jpashop_recap.project1.repository.query;

import jakarta.persistence.EntityManager;
import jpashop_recap.project1.repository.query.dto.OrderItemQueryDto;
import jpashop_recap.project1.repository.query.dto.OrderQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders();

        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });
        return result;
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        List<OrderQueryDto> result = findOrders();

        //v4 방식과 다르게 루프를 돌지 않고 컬렉션을 MAP 한번에 조회
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(toOrderIds(result));

        //기존의 v4 방식은 XToOne으로 쿼리를 탐색한 결과들에 대해 모두 루프를 돌려 매번 쿼리를 발생시키므로 N번의 쿼리 발생.
        //이 방식은 Map으로 한 번만에 컬렉션을 조회하고 그것을 기반으로 컬렉션을 추가하므로 추가적인 쿼리 발생X.
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    //XToOne에 해당하는 것들을 한꺼번에 조회
    private List<OrderQueryDto> findOrders() {
        return em.createQuery("select " +
                "new jpashop_recap.project1.repository.query.dto.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    //XToMany에 해당하는 컬렉션들에 대한 조회
    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery("select new" +
                " jpashop_recap.project1.repository.query.dto.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                " from OrderItem oi" +
                " join oi.item i" +
                " where oi.order.id = : orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        return result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());
    }

    //Map을 활용함으로써 O(1)로 향상
    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery("select new" +
                        " jpashop_recap.project1.repository.query.dto.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                //파라미터로 받은 리스트의 내용에 존재하는 id들에 대해서 모두 쿼리 탐색
                .setParameter("orderIds", orderIds)
                .getResultList();

        //key: OrderId, value: OrderItemQueryDto 객체로 저장된 Map을 만들어 리턴하는 것이다.
        return orderItems.stream()
                .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
    }
}
