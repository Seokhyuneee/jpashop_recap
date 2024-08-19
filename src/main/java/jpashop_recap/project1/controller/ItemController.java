package jpashop_recap.project1.controller;

import jpashop_recap.project1.domain.item.Book;
import jpashop_recap.project1.domain.item.Item;
import jpashop_recap.project1.form.BookForm;
import jpashop_recap.project1.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * 상품 등록 관련 GET, POST
     */
    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form) {
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/";
    }

    /**
     * 상품 목록 조회 GET
     */
    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    /**
     * 상품 수정 GET, POST
     */
    @GetMapping("/items/{itemId}/edit")
    //url 부분에 매번 다른 id로 넘어오는 itemId에 대해서 @PathVariable을 달아준다.
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book item = (Book) itemService.findOne(itemId);
        //이 과정을 하는 이유는 사용자에게는 실제 Entity가 아닌 사용자 전용 form 형식에 적합한 객체를 만들기 위함이다.
        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "/items/updateItemForm";
    }

    @PostMapping("/items/{itemId}/edit")
    //form의 이름으로 저장된 객체를 매개변수로 받는다.
    //즉, GetMapping을 통해 만든 "form"을 updateItemForm.html로 넘기고
    //Post 시 사용자가 수정함으로써 변경된 form 객체를 다시 가져오는 것이다.
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form) {
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());
        return "redirect:/items";
    }

    //GET을 통해 사용자에게 제공되는 경우에는 실제 Entity가 아닌 Form을 attribute해서 제공하고,
    //POST를 통해 실제 Entity 값을 변경해야할 때 사용자가 바꾼 Form 객체를 바탕으로 실제 Entity를 변경하는 작업을 거친다.
}
