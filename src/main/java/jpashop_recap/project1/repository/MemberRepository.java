package jpashop_recap.project1.repository;

import jpashop_recap.project1.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    //기존의 old version에 존재했던 함수들 대다수는 JpaRepository에 기본 옵션으로 존재해서 함수를 선언하지 않아도 됨.

    //JPA가 함수명을 보고 (ex.findBy____ 형태) 알아서 JPQL을 작성해줌.
    //즉, Name만 보고 알아서 select m from Member m where m.name :=name을 작성해줌.
    List<Member> findByName(String name);
}
