package jpashop_recap.project1.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@DiscriminatorValue("A")    //"A"로 Album 엔티티임을 명시한다.
public class Album extends Item {

    private String artist;
    private String etc;
}
