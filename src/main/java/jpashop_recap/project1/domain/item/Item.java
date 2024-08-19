package jpashop_recap.project1.domain.item;

import jakarta.persistence.*;
import jpashop_recap.project1.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)   //상속받는 엔티티들의 모든 데이터들을 하나의 테이블로 병합.
@DiscriminatorColumn(name = "dtype")    //상속 엔티티들의 칼럼 구분 변수
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    //==비즈니스 로직==//
    /**
     * 주문 취소 시 재고 늘리기
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * 주문이 들어오면 재고를 줄이기 (단, 재고가 부족하면 줄이면 안됨)
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("재고가 부족합니다.");
        }
        this.stockQuantity = restStock;
    }
}
