package com.lms.controllers;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.lms.models.UserInfo;
import com.lms.repository.UserInfoRepository;
import com.lms.service.UserInfoService;

@Controller
public class AdminController {

    @Autowired
    UserInfoService userInfoService;
	@Autowired
    UserInfoRepository userInfoRepository;


    @RequestMapping(value = "/user/change-password", method = RequestMethod.GET)
    public ModelAndView changePasswordForm(ModelAndView mav) {

	mav.setViewName("changePassword");
	return mav;
    }

    @RequestMapping(value = "/user/change-password", method = RequestMethod.POST)
    public ModelAndView changePasswordSubmit(ModelAndView mav,@RequestParam("currentPassword") String current_password,
	    @RequestParam("newPassword") String new_password,UserInfo userInfo,HttpSession session) {
	BCryptPasswordEncoder bCryptPassEncoder = new BCryptPasswordEncoder();
	String email = (String)session.getAttribute("mail");
	System.out.println(email);
	// String username = SecurityContextHolder.getContext().getAuthentication().getName();
	// UserInfo userInfo = userInfoService.findUserByEmail(username);
	UserInfo userInfoCheckUser = userInfoRepository.findByEmail(email);
	if(userInfoCheckUser==null){


		mav.addObject("errorMessage", "User does not exist with this email Id");
		mav.setView(new RedirectView("/forgot-password"));
	}
	else{
	    String encodedPassword = userInfoCheckUser.getPassword();
		if (!bCryptPassEncoder.matches(current_password, encodedPassword)) {
			mav.addObject("successMessage", "Current Password entered is wrong!!!");
			mav.setViewName("changePassword");
			return mav;
		}
		userInfoCheckUser.setPassword(new_password);
		userInfoService.saveUserWithPasswordChange(userInfoCheckUser);
		mav.addObject("successMessage", "Password changed Successfully!!!");
		mav.setView(new RedirectView("../user/home"));
	}
	    return mav;
    }
    
    @RequestMapping(value = "/user/manage-users", method = RequestMethod.GET)
    public ModelAndView showManageUsers(ModelAndView mav) {

	List<UserInfo> userList = userInfoService.getUsers();
	mav.addObject("users", userList);
	mav.setViewName("manageUsers");
	return mav;
    }

    @RequestMapping(value = "/user/manage-users/{action}/{id}", method = RequestMethod.GET)
    public ModelAndView manageUsers(ModelAndView mav, @PathVariable("action") String action,
	    @PathVariable("id") int id) {

	UserInfo userInfo = null;
	if (action.equals("edit")) {
	    userInfo = userInfoService.getUserById(id);
	    mav.addObject("userInfo", userInfo);
	    mav.setViewName("editUser");

	} else if (action.equals("delete")) {
	    userInfoService.deleteUser(id);
	    mav.addObject("successMessage", "User removed successfully!!");
	    mav.setView(new RedirectView("/user/manage-users"));
	} else if (action.equals("block")) {
	    userInfoService.blockUser(id);
	    mav.addObject("successMessage", "User blocked successfully!!");
	    mav.setView(new RedirectView("/user/manage-users"));
	} else if (action.equals("unblock")) {
	    userInfoService.unBlockUser(id);
	    mav.addObject("successMessage", "User is active now!!");
	    mav.setView(new RedirectView("/user/manage-users"));
	}

	return mav;
    }
    
    // @RequestMapping(value = "/user/manage-users/save-user-edit", method = RequestMethod.POST)
    // public ModelAndView saveUserEdit(ModelAndView mav, @Valid UserInfo userInfo, BindingResult bindResult) {

	// User userExists = userInfoService.findUserByEmail(user.getEmail());
	// User updateUser = userInfoService.getUserById(user.getId());

	// if (userExists != null && !user.getEmail().equals(updateUser.getEmail())) {
	//     bindResult.rejectValue("email", "error.user", "User already exists with Email id");
	// }

	// if (bindResult.hasErrors()) {
	//     mav.addObject("errorField", bindResult.getFieldError().getField());
	//     mav.addObject("errorMessage", bindResult.getFieldError().getDefaultMessage());
	//     mav.setView(new RedirectView("/docs-app/admin/manage-users/edit/" + user.getId() + ""));
	// } else {
	//     updateUser.setName(user.getName());
	//     updateUser.setLastName(user.getLastName());
	//     updateUser.setEmail(user.getEmail());
	//     updateUser.setPassword(user.getPassword());
	//     updateUser.setRole(user.getRole());
	//     userService.saveUser(updateUser);
	//     mav.addObject("successMessage", "User Details updated successfully!!");
	//     mav.setView(new RedirectView("/docs-app/admin/manage-users"));
	// }
	
}
