package jpashop_recap.project1.repository.query.dto;

import jpashop_recap.project1.api.OrderApiController;
import jpashop_recap.project1.domain.Address;
import jpashop_recap.project1.domain.OrderStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

//@EqualsAndHashCode: Equals + HashCode
//Equals: 두 객체의 내용이 같은가?
//HashCode: 두 객체 자체가 같은 것인가?
//of: of 안에 포함되는 필드에 대해서만 동등성을 비교한다.
@Data
@EqualsAndHashCode(of = "orderId")
public class OrderQueryDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemQueryDto> orderItems;

    public OrderQueryDto(Long orderId, String name, LocalDateTime orderDate,
                         OrderStatus orderStatus, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}