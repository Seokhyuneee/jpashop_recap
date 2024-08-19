package jpashop_recap.project1.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j      //lombok에서 제공. log를 띄우기 위한 log 객체를 생성하지 않도록 해줌.
public class HomeController {

    @RequestMapping("/")    //url에서 '/'으로 요청이 들어온다면 이 컨트롤러를 실행
    public String home() {
        log.info("home controller");
        return "home";      //templates 폴더에 존재하는 home.html의 뷰를 실행시켜줌.
    }
}
