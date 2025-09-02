package com.culturemate.culturemate_api.controller;

import com.culturemate.culturemate_api.CustomUser;
import com.culturemate.culturemate_api.dto.RegisterDto;
import com.culturemate.culturemate_api.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {
  private final MemberService memberService;

  @GetMapping("/register")
  public String memberForm(Model model) {
    model.addAttribute("registerDto", new RegisterDto()); // 빈 객체 전달해서 폼 바인딩
    return "register"; // 타임리프라면 뷰 이름, REST라면 JSON
  }

  @PostMapping("/register")
  String showRegisterForm(@ModelAttribute RegisterDto registerDto){
    memberService.create(registerDto);

    return "redirect:/login";
  }

  @GetMapping("/login")
  public String login(){
    return "login";
  }

  @GetMapping("/my-page")
  public String myPage(Authentication auth){
    CustomUser result = (CustomUser) auth.getPrincipal();
    System.out.println(result.status);
    return "mypage";
  }

}
