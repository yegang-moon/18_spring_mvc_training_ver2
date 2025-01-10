package com.application.trainingVer2.controller;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.application.trainingVer2.dto.MemberDTO;
import com.application.trainingVer2.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/member")
public class MemberController {

	@Value("${file.repo.path}")    	   // application.properties파일의 file.repo.path=c:/member_profile_repository/ 주입
    private String fileRepositoryPath; // C:/member_profile_repository/
	
	@Autowired
	private MemberService memberService;
	
	
	@GetMapping("/main")  // localhost/member/main 요청시 매핑
	public String main()  {
		return "member/main"; // templates/member/main.html 파일로 포워딩
	}
	
	
	@GetMapping("/register")  // localhost/member/main 요청시 매핑
	public String register() {
		return "member/register"; // templates/member/register.html 파일로 포워딩
	}
	
	
	@PostMapping("/register") //register.html 파일에서 회원가입 진행할 때 매핑
	@ResponseBody
						  // 파일은 MultipartFile로 받는다.								 // DTO관련 데이터는 DTO로 전송받는다.
	public String register(@RequestParam("uploadProfile") MultipartFile uploadProfile , @ModelAttribute MemberDTO memberDTO) throws IllegalStateException, IOException  {
		
		memberService.createMember(uploadProfile, memberDTO); // 관련 로직은 서비스에서 처리하기 위하여 포워딩한다.
		
		String jsScript = """
				<script>
					alert('회원가입 되었습니다.');
					location.href='/member/main';
				</script>
				""";
		
		return jsScript; // /member/main으로 포워딩
		
	}
	
	
	@PostMapping("/validId") // localhost/member/validId 요청시 매핑
	@ResponseBody
	public String validId(@RequestParam("memberId") String memberId) { 
		return memberService.checkValidId(memberId); // memberId를 전달받아 중복체크한 결과 ('y' or 'n')을 register.html파일의 AJAX success 콜백함수로 반환
	}
	
	
	@GetMapping("/login") // localhost/member/login 요청시 매핑
	public String login()  {
		return "member/login"; // templates/member/login.html 파일로 포워딩
	}
	
	
	@PostMapping("/login") //login.html 파일에서 로그인 진행할 때 매핑
	@ResponseBody		// 전송된 데이터를 받는다.			session에 등록하기 위해 작성한다.
	public String login(@RequestBody MemberDTO memberDTO , HttpServletRequest request) {
		
		String isValidMember = "n";					// 초기값 : 인증 x
		if (memberService.login(memberDTO)) {		// memberService에서 인증여부를 확인한 후 인증이 되었을 경우(return true)
			
			HttpSession session = request.getSession();					
			session.setAttribute("memberId", memberDTO.getMemberId());  // session 객체에 memberId를 저장한다.
			
			isValidMember = "y";
			
			System.out.println(session.getId());
			
		} 
		
		return isValidMember; // 로그인 여부 ('y' or 'n')을 login.html파일의 AJAX success 콜백함수로 반환
		
	}
	
	
	@GetMapping("/logout") // localhost/member/logout 요청시 매핑
	@ResponseBody
	public String logout(HttpServletRequest request) {
		
		HttpSession session = request.getSession(); 
		session.invalidate(); // 세션 삭제
		
		String jsScript = """
				<script>
					alert('로그아웃 되었습니다.');
					location.href='/member/main';
				</script>
				""";
		
		return jsScript;  // /member/main으로 포워딩
		
	}
	
	
	@GetMapping("/update") // localhost/member/update 요청시 매핑
	public String update(HttpServletRequest request , Model model) {
		
		HttpSession session = request.getSession();	
		// Session에서 memberId정보를 가져와 memberDTO를 조회한다.
		model.addAttribute("memberDTO" , memberService.getMemberDetail((String)session.getAttribute("memberId")));
		
		return "member/updateMember"; // templates/member/update 화면으로 포워딩(memberDTO 데이터포함)
		
	}	
	
	
	@PostMapping("/update") // update.html 파일에서 수정을 진행할 때 매핑
	@ResponseBody
							// 파일은 MultipartFile로 받는다.						    // DTO관련 데이터는 DTO로 전송받는다.
	public String update(@RequestParam("uploadProfile") MultipartFile uploadProfile , @ModelAttribute MemberDTO memberDTO) throws IllegalStateException, IOException  {
		
		memberService.updateMember(uploadProfile , memberDTO); // 관련 로직은 서비스에서 처리하기 위하여 포워딩한다.
		
		String jsScript = """
				<script>
					alert('수정 되었습니다.');
					location.href='/member/main';
				</script>
				""";
		
		return jsScript; // /member/main으로 포워딩
		 
	}
	
	
	@GetMapping("/thumbnails") // localhost/member/thumbnails 요청시 매핑
    @ResponseBody
    public Resource thumbnails(@RequestParam("fileName") String fileName) throws MalformedURLException{
        return new UrlResource("file:" + fileRepositoryPath + fileName); // 전달된 파일명으로 썸네일 객체를 반환한다.
    }
	
	
	@GetMapping("/delete")  // localhost/member/delete 요청시 매핑
	public String delete() {
		return "member/deleteMember"; // templates/member/delete 화면으로 포워딩
	}
	
	
	@PostMapping("/delete") // delete.html 파일에서 삭제를 진행할 때 매핑
	@ResponseBody
	public String delete(HttpServletRequest request)  {
		
		
		HttpSession session = request.getSession();
		memberService.deleteMember((String)session.getAttribute("memberId")); // Session 객체에서 memberId를 조회하여 Service로 전달한다.
		session.invalidate(); // 세션 삭제
		
		String jsScript = """
				<script>
					alert('탈퇴 되었습니다.');
					location.href='/member/main';
				</script>
				""";
		
		return jsScript; // /member/main으로 포워딩
		
	}
	
}
