package jpashop_recap.project1.repository;

import jakarta.persistence.EntityManager;
import jpashop_recap.project1.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor    //final로 지정된 필드에 대해서 자동으로 생성자를 만든다.
public class MemberRepository_version1 {

    //엔티티 객체와 DB 간의 상호작용에 사용되는 인터페이스
    //1) CRUD  2) 쿼리 실행  3) 트랜잭션 관리  4) 영속성 컨텍스트 관리
    //영속성 컨텍스트란 실제 DB에 데이터가 업데이트되기 전의 임시 저장소 같은 느낌이다.
    //일반적으로, 트랜잭션 커밋이 일어날 때, 실제 DB에 데이터가 업데이트된다.
    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);     //CRUD 중 하나. 엔티티를 영속성 컨텍스트에 추가한다.
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);   //CRUD 중 하나. id와 일치하는 엔티티를 하나 찾는다.
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();   //JPQL은 SQL과 다르게 엔티티 단위로 탐색한다.
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)     //setParameter를 통해 재사용성을 증가
                .getResultList();
    }
}
