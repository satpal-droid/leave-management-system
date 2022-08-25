package com.lms.controllers;


import java.util.Random;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.lms.EmailService.EmailSenderService;
import com.lms.models.UserInfo;

import com.lms.service.LeaveManageService;
import com.lms.service.UserInfoService;



/**
 * This controller will provide the basic operations fo users. Like
 * signing-in,registering a new user.
 * 
 * @author nk
 *
 */

@Controller
public class LoginController {

    @Autowired
    private UserInfoService userInfoService;
   

    @Autowired
    LeaveManageService leaveManageService;

    @Autowired
    EmailSenderService emailSenderService;



    /**
     * This method opens up the login page if user is not authenticated
     * otherwise redirects the user to user home page.
     * 
     * @return
     */ 
    @GetMapping("/login")
    public ModelAndView loginn(ModelAndView mav) {

        mav.setViewName("login");
        return mav;
    }
    @RequestMapping(value = {"/login" }, method = RequestMethod.POST)
    public ModelAndView login(ModelAndView mav) {

	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	UserInfo userInfo = userInfoService.findUserByEmail(auth.getName());

	mav.addObject("userInfo", userInfo);
	if (!(auth instanceof AnonymousAuthenticationToken)) {
        mav.addObject("successMessage", "You logged in successfully!!");
	    mav.setViewName("home");
	    return mav;
	}
	mav.setViewName("login");
	return mav;
    }
    /**
     * Opens the registration page to register a new user.
     * 
     * @return ModelAndView
     */
    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public ModelAndView registration(ModelAndView mav) {

	UserInfo userInfo = new UserInfo();
	mav.addObject("userInfo", userInfo);
	mav.setViewName("registration");
	return mav;
    }

    /**
     * Gets the form input from registration page and adds the user to the
     * database.
     * 
     * @param user
     * @param bindResult
     * @return ModelAndView
     */
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(ModelAndView mav, @Valid UserInfo userInfo, BindingResult bindResult) {

	UserInfo userExists = userInfoService.findUserByEmail(userInfo.getEmail());

	if (userExists != null) {
	    bindResult.rejectValue("email", "error.user", "User already exists with Email id");
	}

	if (bindResult.hasErrors()) {
	    mav.setViewName("registration");
	} else {
	    userInfoService.saveUser(userInfo);
	    mav.addObject("successMessage", "User registered successfully! Awaiting for Manager approval!!");
	    mav.addObject("userInfo", new UserInfo());
        mav.setViewName("login");
	}
	return mav;
    }

    /**
     * Shows the admin page after user authentication is done.
     * 
     * @param request
     * @return ModelAndView 
     * @throws JSONException
     */
    @RequestMapping(value = "/user/home", method = RequestMethod.GET)
    public ModelAndView home(ModelAndView mav, HttpServletRequest request) throws Exception {
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	UserInfo userInfo1 = userInfoService.findUserByEmail(auth.getName());
	request.getSession().setAttribute("userInfo", userInfo1);
    mav.addObject("userInfo2",userInfo1);
	mav.setViewName("home");
	return mav;
    }

    @RequestMapping(value ="/forgot-password", method = RequestMethod.GET)
    public ModelAndView forgotPasswordForm(ModelAndView mv){

        mv.setViewName("forgotPassword");
        return mv;
    }
     
    @RequestMapping(value ="/forgot-password", method = RequestMethod.POST)
    public ModelAndView forgotPassword(ModelAndView mv , UserInfo userInfo , @RequestParam("email") String email , BindingResult bindResult,HttpSession session ){

        UserInfo userInfo1 = userInfoService.findUserByEmail(email); 
        String existsEmail = (userInfo1.getEmail());
        if(existsEmail==null){
            mv.addObject("message", "Email Id does not exists.Please Enter the Correct Email!");
            mv.setViewName("forgotPassword");
        }
        else{
             Random random = new Random();
             int otp = random.nextInt(99999);
            //  String sOtp = String.valueOf(otp);
             String text = "This is your"+" "+otp;
             emailSenderService.sendEmail(existsEmail,"Your OTP", text);
             session.setAttribute("sessionOtp", otp);
             session.setAttribute("mail", existsEmail);  
             mv.setView(new RedirectView("/verify-OTP"));
        }
             return mv;
    }
    @RequestMapping(value ="/verify-OTP", method = RequestMethod.POST)
    public ModelAndView verifyOTPForm(ModelAndView mv,HttpSession session,@RequestParam("otp") int otp){
        
         session.getAttribute("sessionOtp");
         Integer intOtp = (int)(session.getAttribute("sessionOtp"));
         String email = (String)session.getAttribute("mail");      
	     System.out.println(email);
         System.out.println(intOtp);
         if(otp==intOtp){

            mv.setView(new RedirectView("/user/change-password"));

         }
         else{

            mv.addObject("message", "Wrong OTP");
            mv.setViewName("verifyOTP");

         }
         return mv;
    }
    @RequestMapping(value ="/verify-OTP", method = RequestMethod.GET)
    public ModelAndView verifyOTP(ModelAndView mv){

         mv.setViewName("verifyOTP");
         return mv;
    }


}
