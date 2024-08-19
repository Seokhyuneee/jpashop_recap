package jpashop_recap.project1.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    //임베디드 타입은 한 번 인스턴스 초기화를 하고 나면 변경 불가능하게 만들어야 한다.
    //protected로 설정하여 불변성을 유지할 수 있다.
    protected Address() {}

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
