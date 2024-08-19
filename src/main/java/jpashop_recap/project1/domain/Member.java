package jpashop_recap.project1.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    //Id : 해당 변수를 pk로 지정. GeneratedValue : 번호를 자동으로 알아서 생성
    //Column : id 변수를 member_id로 구분해줌. (다른 엔티티의 id와 구별하기 위함.)
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    //NotEmpty : null을 할 수 없는 필수적으로 필요한 변수
    @NotEmpty
    private String name;

    //Embedded : embeddable한 엔티티인 Address 타입임을 명시
    @Embedded
    private Address address;

    //OneToMany : 1대다. Order 엔티티에 존재하는 member 변수와 매핑된다.
    //Order에서 수많은 Member 객체를 만들어내면, 그 객체를 List 형식으로 보관해주는 역할.
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
