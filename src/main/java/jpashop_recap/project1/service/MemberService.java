package jpashop_recap.project1.service;

import jpashop_recap.project1.domain.Member;
import jpashop_recap.project1.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 1) 회원가입  2) 전체 회원 조회
 */
@Service
@Transactional(readOnly = true)     //default를 읽기 전용으로 설정.(트랜잭션이 일어나지 않도록 한다.)
@RequiredArgsConstructor
public class MemberService {

    //생성자 의존관계 주입. 생성자가 하나면 @Autowired 생략해도 무관.
    @Autowired
    private final MemberRepository memberRepository;

    /**
     * 기능1 - 회원가입
     */
    @Transactional //이 메소드는 변경이 가능하도록 한다.
    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 기능2 - 전체 회원 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long id) {
        //return memberRepository.findOne(id);      old_version 기준
        return memberRepository.findById(id).get();
    }

    /**
     * 회원 수정 - 변경 감지 기능 사용 (병합X)
     */
    @Transactional
    public void update(Long id, String name) {
        //Member member = memberRepository.findOne(id);     old_version 기준
        Member member = memberRepository.findById(id).get();
        member.setName(name);
    }

}
