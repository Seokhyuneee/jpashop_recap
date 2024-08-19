package jpashop_recap.project1.controller;

import jakarta.validation.Valid;
import jpashop_recap.project1.domain.Address;
import jpashop_recap.project1.domain.Member;
import jpashop_recap.project1.service.MemberService;
import jpashop_recap.project1.form.MemberForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원 가입 기능과 관련된 GET, POST
     */
    @GetMapping(value = "/members/new")
    public String createForm(Model model) {
        //addAttribute를 통해 key: memberForm value: MemberForm 객체 형태로 저장할 수 있다.
        //이 코드 덕분에 View 코드(html)에서 ${memberForm}에 대한 부분이 필요한 MemberForm 객체로 대체된다.
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping(value = "/members/new")
    //@Valid: 매개변수로 받은 form이 타당한지를 검증.
    //BindingResult: 기존에는 에러가 발생하면 코드를 중단하고 에러 페이지로 보내는데 에러가 발생해도 코드를 진행시킨다.
    //에러 발생 시 의도한 view를 띄우기 위해서 사용된다.
    public String create(@Valid MemberForm form, BindingResult result) {
        //에러가 발생하면 다시 createMemberForm으로 이동한다.
        if (result.hasErrors()) {
            return "members/createMemberForm";
        }

        Member member = new Member();
        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";    //다시 '/' 경로로 이동하라는 의미
    }

    /**
     * 회원 목록 조회와 관련된 GET
     */
    @GetMapping("/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
