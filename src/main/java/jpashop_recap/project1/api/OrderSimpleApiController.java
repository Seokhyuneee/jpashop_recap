package jpashop_recap.project1.api;

import jpashop_recap.project1.domain.Address;
import jpashop_recap.project1.domain.Order;
import jpashop_recap.project1.domain.OrderSearch;
import jpashop_recap.project1.domain.OrderStatus;
import jpashop_recap.project1.repository.OrderRepository;
import jpashop_recap.project1.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * XToOne 방식일 때 최적화하는 방법.
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    /**
     * 간단한 주문 조회 Version2 - DTO로 매핑
     * Fetch Join을 사용하지 않는 방식으로, N+1 문제가 발생하는 단점이 있다.
     */
    /* N+1 문제란?
       지연 로딩 방식으로 인해 실제로 데이터를 필요로 할 때 쿼리를 통해 실제 데이터베이스를 탐색하는데,
       이 때 발생하는 쿼리 발생 횟수를 의미한다. (많을수록 비효율적임 -> 연관관계가 많으면 기하급수적으로 늘어남.)
       맨 처음 모든 주문을 조회하는데 쿼리 1번 실행.
       각 주문에 대한 Member 테이블 조회에 쿼리 실행.
       각 주문에 대한 Delivery 테이블 조회에 쿼리 실행.
       단, 지연로딩은 영속성 컨텍스트에서 조회하므로, 이미 조회된 쿼리의 경우는 생략 가능
       (ex. 주문이 모두 UserA에 대한 주문이라면 쿼리가 N번이 아닌 1번이 일어난다.)
    */
    @GetMapping("/api/v2/simple-orders")
    public Result ordersV2() {
        List<Order> orders = orderService.findOrders(new OrderSearch());
        List<SimpleOrderDto> collect = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return new Result(collect);
    }

    /**
     * 간단한 주문 조회 Version3 - Version2 + Feteh Join
     * N+1 문제를 해결할 수 있다.
     * => 테이블을 가져올 때 해당 order와 관련된 다른 엔티티들을 한 번에 가져오는 방식을 사용하기 때문.
     */
    @GetMapping("/api/v3/simple-orders")
    public Result ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> collect = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return new Result(collect);
    }

    /**
     * Version4도 존재하는데, 이건 강의자료로 별도로 공부하기.
     * => 웬만한 문제는 Version3으로 해결이 되는데 특수한 경우 사용하는 버전이기 때문이다.
     */

    //-------------------------------------- DTO --------------------------------------

    @Data
    static class SimpleOrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
