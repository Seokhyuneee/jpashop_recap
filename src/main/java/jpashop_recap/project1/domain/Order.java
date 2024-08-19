package jpashop_recap.project1.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") //기본적으로 엔티티 이름이 DB 테이블 이름으로 사용된다.
//테이블명을 바꾼 이유는 SQL 작성에 ORDER 명령어가 존재하기 때문에 혼란을 피하기 위해 변경하였다.
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    //ManyToOne : fetch에는 EAGER 방식과 LAZY 방식이 있는데, XToOne은 default로 EAGER로 설정되어 있기에 LAZY로 바꿈.
    //지연 로딩으로 설정하는 이유는 나중에 데이터베이스에서 엔티티를 불러올 때, 모든 엔티티를 가져오는 것이 아니라
    //엔티티를 필요로 하는 순간에 엔티티를 가져옴으로써 불러오는 쿼리 수를 줄여 성능을 향상.
    //JoinColumn : member_id와 매핑시켜 Member 객체를 형성한다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    //OneToOne일 때에는 한 곳에서는 mappedBy를 해주고, 한 곳에서는 JoinColumn을 해준다.
    //Delivery보다는 Order에서 delivery의 엔티티를 관리하는 것이 편하므로, Order 엔티티가 Many 역할을 하게 둠.
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    //cascade : 부모 엔티티의 변경에 따라 자식 엔티티의 변경이 이루어지도록 한다.
    //cascade를 필요로 하는 상황 : 강한 종속 관계일 때
    //ex) Order가 있으면 반드시 OrderItem 엔티티도 형성되어야 한다.
    //cascade를 필요로 하지 않는 상황 : 종속 관계가 그리 강하지 않을 때
    //ex) Member와 Order도 1대다 관계였지만, 멤버가 있다고 반드시 Order가 있어야 하는 것은 아니다.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime orderDate;

    //Enum 객체를 활용하는 변수임을 선언.
    //STRING으로 지정하면 원하는 상태와 Enum에 선언된 타입의 String이 일치할 때 데이터를 가져올 수 있다.
    @Enumerated(EnumType.STRING)
    private OrderStatus status;


    //==연관관계 메서드==//
    //Order Entity가 Member와 Delivery Entity 통제권을 가지고 있어서 Order를 통해 초기화해준다.
    //또한, 주문에 포함되는 모든 item들을 한번에 파악할 수 있도록 addOrderItem도 만든다.
    //즉, 앞으로 Member, Delivery, OrderItem 생성과 추가에 관한 로직은 항상 Order가 담당하도록 한다.
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);   //Member 엔티티에 존재하는 List에 Order 엔티티 추가.
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);    //Delivery에 매핑되는 Order를 set해준다.
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);   //Order에 존재하는 List에 OrderItem 정보를 추가하고, 해당 OrderItem을 set해준다.
    }

    //==생성 메서드==//
    //생성자를 직접 사용하는 것보다 더 명시적이고, 유지 보수가 용이하여 사용한다.
    //Order 객체를 생성하고 초기화하는데 필요한 로직들을 포함한다.
    //OrderItem은 여러개 Order에 추가될 수 있으므로 ...으로 표기한다.
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.ORDER);
        return order;
    }

    //==비즈니스 로직==//
    //사용자의 행동에 따라 Entity의 상태가 변할 때의 규칙과 로직을 구현하는 메서드
    /**
     * 주문 취소
     */
    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL);
        //Order에는 Item에 대한 변수가 없으므로 OrderItem Entity를 거쳐 재고를 다시 늘린다.
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

    /**
     * 전체 금액 조회 로직
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
}
