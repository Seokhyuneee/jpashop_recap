package jpashop_recap.project1.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@DiscriminatorValue("B")    //"B"로 Album 엔티티임을 명시한다.
public class Book extends Item {

    private String author;
    private String isbn;
}
