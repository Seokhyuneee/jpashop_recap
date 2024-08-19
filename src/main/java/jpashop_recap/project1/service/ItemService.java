package jpashop_recap.project1.service;

import jpashop_recap.project1.domain.item.Item;
import jpashop_recap.project1.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 1) 상품 추가  2) 전체 상품 조회
 */

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    /**
     * 기능1 - 상품 추가
     */
    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    /**
     * 기능2 - 전체 상품 조회
     */
    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

    /**
     * 수정을 원할 때는 트랜잭션이 있는 서비스 계층에서 엔티티를 수정하도록 한다.
     */
    //준영속 엔티티(이미 한번 em을 거친 엔티티)를 수정하는 방법으로 1) 변경 감지 기능  2) merge(병합) 기능이 존재하는데,
    //merge 방식은 만약, 주소를 입력하지 않으면 주소의 값은 null로 저장되는 경우를 발생시키므로 변경 감지 기능을 선호한다.
    //따라서, 트랜잭션이 있는 서비스 계층에서 엔티티의 수정하려는 부분에 대해 함수를 구현하고 커밋시킨다.
    @Transactional
    public void updateItem(Long id, String name, int price, int stockQuantity) {
        Item item = itemRepository.findOne(id);
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
    }

}
