package jpashop_recap.project1.form;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * 사용자에게 실제로 보여지는 요소들을 다룬 것.
 * Entity와는 다른 개념이므로 별도로 Form을 만들어줘야 한다.
 */
@Getter @Setter
public class MemberForm {

    @NotEmpty(message = "회원 이름은 필수입니다.")
    private String name;

    private String city;
    private String street;
    private String zipcode;
}
