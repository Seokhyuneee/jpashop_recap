package jpashop_recap.project1.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpashop_recap.project1.domain.*;
import jpashop_recap.project1.repository.OrderRepository;
import jpashop_recap.project1.repository.query.OrderQueryRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * XToMany 방식일 때 최적화하는 방법.
 */
@RestController
@RequiredArgsConstructor
public class OrderApiController {
    /**
     * version1: 엔티티 직접 노출 (안할거임)
     * version2: fetch join 사용하지 않은 DTO 변환
     * version3: fetch join 사용한 DTO 변환
     * version4: JPA에서 DTO로 바로 조회. 컬렉션 N 조회 (페이징 가능)
     * version5: JPA에서 DTO로 바로 조회. 컬렉션 1 조회 (페이징 가능)
     * version6: JPA에서 DTO로 바로 조회. 플랫 데이터 (페이징 불가능)
     */

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/api/v2/orders")
    public Result ordersV2() {
        List<Order> orders = orderRepository.findAll(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return new Result(collect);
    }

    /**
     * 단점: 페이징이 불가능하다.
     * 수많은 데이터가 데이터베이스에 저장되어 있을 때, order를 10개씩 페이징하여 가져오고 싶은데,
     * 각 order에 여러 개의 orderItem이 존재함으로써 데이터베이스의 row가 증가함에 따라 원하는 데이터를 얻지 못할 수 있다.
     * => 페이징 불가능의 문제점은 결국 필요로 하는 데이터를 가져오지 못하고 결국 전체 데이터를 로드하게 되므로
     * 성능, 비용적 저하가 나타난다.
     * 해결법 1) 페치 조인 대신 BatchSize 설정
     */
    @GetMapping("/api/v3/orders")
    public Result ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return new Result(collect);
    }

    /**
     * BatchSize를 활용한 성능 최적화
     */
    @GetMapping("/api/v3.1/orders")
    //@RequestParam: URL에서 제공된 쿼리 파라미터를 매핑하는 것.
    //ex) /api/v3.1/orders?offset=20 으로 입력 시, offset에 20이 저장되어 코드 실행.
    //defaultValue: 0 => 만약 쿼리 파라미터가 존재하지 않으면 default는 0으로 둔다는 뜻.
    public Result ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery_2(offset, limit);
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return new Result(collect);
    }

    @GetMapping("/api/v4/orders")
    public Result ordersV4() {
        return new Result(orderQueryRepository.findOrderQueryDtos());
    }

    @GetMapping("/api/v5/orders")
    public Result ordersV5() {
        return new Result(orderQueryRepository.findAllByDto_optimization());
    }

    /**
     * version6: 쿼리를 한번에 조인할 수는 있으나, 페이징이 불가능하고 중복 데이터 문제로 인해
     * version5보다 느릴 수도 있다. 따라서, 별도로 필요할 때 공부할 예정.
     */

    //-------------------------------------- DTO --------------------------------------

    @Data
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;      //새로 추가

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getMember().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(oi -> new OrderItemDto(oi))
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
