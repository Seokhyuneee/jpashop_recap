package jpashop_recap.project1.api;

import jakarta.validation.Valid;
import jpashop_recap.project1.domain.Member;
import jpashop_recap.project1.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

//@ResponseBody + @Controller를 합친 애노테이션
//해당 클래스에서 나온 객체는 자동으로 JSON 형태로 변환되어 클라이언트에 전송된다.
//ResponseBody란? - 일반적인 Controller는 view에 렌더링하여 보여지는 방식이었는데,
//굳이 HTML로 return하지 않고, 반환되는 객체 양식을 HTML로 그대로 작성되게 한다.
//즉, 사용자는 어떠한 정보를 요청하면 JSON 형식으로 받는데 그것을 최적화하기 위해 사용되는 애노테이션이다.
@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /*
     * version1) 매개변수로 직접 엔티티를 받는 방식.
     * 그러나, 실무에서 엔티티를 노출시키는 것은 위험하고, 하나의 엔티티에 대해서 다양한 방식으로 API를 만드는 것이 어렵다.
     * 따라서, 별도의 DTO를 파라미터로 받는 것을 추천한다.
     * DTO: 전달하고자 하는 데이터들을 종합하여 제공하는 객체.
     * 엔티티와 분리되어 안전하고, 요구사항에 맞게 다양한 방식으로 전달이 가능하다.
     */

    /**
     * 회원 생성 및 수정 version2 - DTO를 RequestBody에 매핑
     */
    @PostMapping("/api/v2/members")
    //요청으로 들어온 JSON 형식 request를 통해 응답을 제공해준다.
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id,
                                               @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        Member member = memberService.findOne(id);
        return new UpdateMemberResponse(member.getId(), member.getName());
    }

    /**
     * 회원 조회 version2 - DTO (매나 version1은 엔티티 노출 방식이라 사용X)
     */
    @GetMapping("/api/v2/members")
    public Result membersV2() {
        List<Member> members = memberService.findMembers();
        //stream: 객체에 대해서 연산 수행을 가능하게 함(ex. map)
        //members에 존재하는 여러 Member 객체에 대해 모두 MemberDto 객체를 생성하고, 그것들을 List로 저장한다.
        List<MemberDto> collect = members.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect);
    }

   //-------------------------------------- DTO --------------------------------------

    /**
     * 생성 요청 DTO
     */
    @Data   //자동으로 GETTER, SETTER를 제공하고, RequiredArgsConstructor 역할을 해준다.
    static class CreateMemberRequest {
        private String name;
    }

    /**
     * 생성 응답 DTO
     */
    @Data
    @AllArgsConstructor //필드에 대한 생성자를 자동 생성해준다. 간단한 경우에만 사용하고, 실제 엔티티에는 사용X
    static class CreateMemberResponse {
        private Long id;
    }

    /**
     * 사용자 수정 요청 DTO
     */
    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    /**
     * 사용자 수정 응답 DTO
     */
    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    /**
     * 조회용 DTO
     */
    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    /**
     * Result 클래스 - 어떤 DTO 타입이라도 마지막에 Result 타입으로 바꿈으로써 동일한 타입으로 사용자에게 리턴
     * 많은 데이터 객체들을 하나로 만들어서 제공할 수 있는 장점이 있다.
     */
    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
