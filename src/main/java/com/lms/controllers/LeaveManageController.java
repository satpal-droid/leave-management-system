package com.lms.controllers;

// import java.text.SimpleDateFormat;
// import java.util.Calendar;
// import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

// import org.json.JSONArray;
// import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.support.RequestPartServletServerHttpRequest;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import com.lms.models.LeaveDetails;
import com.lms.models.UserInfo;
import com.lms.pdf.UserPDFExporter;
import com.lms.repository.LeaveManageRepository;
import com.lms.service.LeaveManageService;
import com.lms.service.UserInfoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import com.lowagie.text.DocumentException;

@Controller
public class LeaveManageController {

    @Autowired
    private LeaveManageService leaveManageService;

    @Autowired
    private UserInfoService userInfoService;

	@Autowired
    private LeaveManageRepository leaveManageRepository;

	
    @RequestMapping(value = "/user/apply-leave", method = RequestMethod.GET)
    public ModelAndView applyLeave(ModelAndView mav,HttpServletRequest request,LeaveDetails leaveDetails) {

	

	mav.addObject("leaveDetails", new LeaveDetails());
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	UserInfo userInfo = userInfoService.findUserByEmail(auth.getName());
	request.getSession().setAttribute("userInfo", userInfo);
	Integer activeLeaves = leaveManageService.countAllLeaves(userInfo.getEmail());
	Integer totalLeaves  = leaveManageRepository.totalLeaves();
	Integer rejectedLeaves = leaveManageService.countAllRejectedLeaves(userInfo.getEmail());

	if((activeLeaves == null)&&(rejectedLeaves == null)){

		Integer activeLeaves1 = 0;
		Integer balancedLeaves1 = 8;
		Integer rejectedLeaves1 = 0;


		mav.addObject("rejectedLeaves", rejectedLeaves1);
		mav.addObject("balancedLeaves", balancedLeaves1);
		mav.addObject("activeLeaves", activeLeaves1);
		mav.addObject("totalLeaves", totalLeaves);
		mav.addObject("userInfo", userInfo);
		mav.setViewName("applyLeave");
	
	}
	else if((activeLeaves != null)&&(rejectedLeaves==null)){

		Integer activeLeaves1 = leaveManageService.countAllLeaves(userInfo.getEmail());;
		Integer balancedLeaves1 =(totalLeaves-activeLeaves) ;
		Integer rejectedLeaves1 = 0;


		mav.addObject("rejectedLeaves", rejectedLeaves1);
		mav.addObject("balancedLeaves", balancedLeaves1);
		mav.addObject("activeLeaves", activeLeaves1);
		mav.addObject("totalLeaves", totalLeaves);
		mav.addObject("userInfo", userInfo);
		mav.setViewName("applyLeave");
	} 
	else if((activeLeaves == null)&&(rejectedLeaves!=null)){

		Integer activeLeaves1 = 0;
		Integer balancedLeaves1 = 8;
		Integer rejectedLeaves1 = leaveManageService.countAllRejectedLeaves(userInfo.getEmail());;


		mav.addObject("rejectedLeaves", rejectedLeaves1);
		mav.addObject("balancedLeaves", balancedLeaves1);
		mav.addObject("activeLeaves", activeLeaves1);
		mav.addObject("totalLeaves", totalLeaves);
		mav.addObject("userInfo", userInfo);
		mav.setViewName("applyLeave");
	} 
	else if((rejectedLeaves!=null)||(activeLeaves != null))
	{

	    Integer balancedLeaves = (totalLeaves - activeLeaves);
		mav.addObject("rejectedLeaves", rejectedLeaves);
		mav.addObject("balancedLeaves", balancedLeaves);
		mav.addObject("activeLeaves", activeLeaves);
		mav.addObject("totalLeaves", totalLeaves);
		mav.addObject("userInfo", userInfo);
		mav.setViewName("applyLeave");
	}
	
	

	return mav;
    }
	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/user/apply-leave", method = RequestMethod.POST)
    public ModelAndView submitApplyLeave(ModelAndView mav, @Valid LeaveDetails leaveDetails,
	    BindingResult bindingResult,HttpServletRequest request,@RequestParam()) {

	UserInfo userInfo = userInfoService.getUserInfo();
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	UserInfo userInfo1 = userInfoService.findUserByEmail(auth.getName());
	request.getSession().setAttribute("userInfo", userInfo);
	Integer totalLeaves = leaveManageRepository.totalLeaves();
    Integer activeLeaves = leaveManageService.countAllLeaves(userInfo1.getEmail());
	Integer duration = leaveDetails.getToDate().getDate() - leaveDetails.getFromDate().getDate(); 
	System.out.println(duration);
	if(activeLeaves==null){
     activeLeaves=0;		
	}
	 if (bindingResult.hasErrors()) { 
		mav.setViewName("applyLeave");
	}

	else if(activeLeaves>totalLeaves){

        mav.addObject("errorMessage", "you cannot take leaves more than 8.");
		mav.setViewName("applyLeave");
	}

     else {
	    leaveDetails.setUsername(userInfo.getEmail());
	    leaveDetails.setEmployeeName(userInfo.getFirstName() + " " + userInfo.getLastName());
	    leaveManageService.applyLeave(leaveDetails);
	    mav.addObject("successMessage", "Your Leave Request is registered!");
	    mav.setView(new RedirectView("/user/my-leaves"));
	 }
		return mav;
	
	
    }

    // @RequestMapping(value = "/user/get-all-leaves", method = RequestMethod.GET)
    // public @ResponseBody String getAllLeaves(@RequestParam(value = "pending", defaultValue = "false") boolean pending,
	//     @RequestParam(value = "accepted", defaultValue = "false") boolean accepted,
	//     @RequestParam(value = "rejected", defaultValue = "false") boolean rejected) throws Exception {

	// Iterator<LeaveDetails> iterator = leaveManageService.getAllLeaves().iterator();
	// if (pending || accepted || rejected)
	//     iterator = leaveManageService.getAllLeavesOnStatus(pending, accepted, rejected).iterator();
	// JSONArray jsonArr = new JSONArray();
	// SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD");
	// Calendar calendar = Calendar.getInstance();

	// while (iterator.hasNext()) {
	//     LeaveDetails leaveDetails = iterator.next();
	//     calendar.setTime(leaveDetails.getToDate());
	//     calendar.add(Calendar.DATE, 1);

	//     JSONObject jsonObj = new JSONObject();
	//     jsonObj.put("title", leaveDetails.getEmployeeName());
	//     jsonObj.put("start", dateFormat.format(leaveDetails.getFromDate()));
	//     jsonObj.put("end", dateFormat.format(calendar.getTime()));
	//     if (leaveDetails.isActive())
	// 	jsonObj.put("color", "#0878af");
	//     if (!leaveDetails.isActive() && leaveDetails.isAcceptRejectFlag())
	// 	jsonObj.put("color", "green");
	//     if (!leaveDetails.isActive() && !leaveDetails.isAcceptRejectFlag())
	// 	jsonObj.put("color", "red");
	//     jsonArr.put(jsonObj);
	// }

	// return jsonArr.toString();
    // }
    
    @RequestMapping(value="/user/manage-leaves",method= RequestMethod.GET)
    public ModelAndView manageLeaves(ModelAndView mav) {
	List<LeaveDetails> leavesList = leaveManageService.getAllActiveLeaves();
	mav.addObject("leavesList", leavesList);
	mav.setViewName("manageLeaves");
	return mav;
    }

    @RequestMapping(value = "/user/manage-leaves/{action}/{id}", method = RequestMethod.GET)
    public ModelAndView acceptOrRejectLeaves(ModelAndView mav, @PathVariable("action") String action,
	    @PathVariable("id") Integer id) {
	LeaveDetails leaveDetails = leaveManageService.getLeaveDetailsOnId(id);
	if (action.equals("accept")) {
	    leaveDetails.setAcceptRejectFlag(true);
	    leaveDetails.setActive(false);
		mav.setView(new RedirectView("/user/manage-leaves"));
	} else if (action.equals("reject")) {
	    leaveDetails.setAcceptRejectFlag(false);
	    leaveDetails.setActive(false);
		mav.setView(new RedirectView("/LEAVE-MANAGEMENT-SYSTEM/user/manage-leaves"));
	
	}
	leaveManageService.updateLeaveDetails(leaveDetails);
	mav.addObject("successMessage", "Updated Successfully!");
	mav.setView(new RedirectView("/user/manage-leaves"));
	return mav;
    }

    @RequestMapping(value = "/user/my-leaves", method = RequestMethod.GET) 
    public ModelAndView showMyLeaves(ModelAndView mav) {

	UserInfo userInfo = userInfoService.getUserInfo();
	List<LeaveDetails> leavesList = leaveManageService.getAllLeavesOfUser(userInfo.getEmail());
	mav.addObject("leavesList", leavesList);
	mav.setViewName("myLeaves");
	return mav;
    }

    @RequestMapping(value="/user/export/pdf", method = RequestMethod.GET)
    public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
         
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".pdf";
		response.setHeader(headerKey, headerValue);
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
	    UserInfo userInfo = userInfoService.findUserByEmail(username);
        List<LeaveDetails> leaveDetails = leaveManageService.getAllLeavesOfUser(userInfo.getEmail());  
        UserPDFExporter exporter = new UserPDFExporter(leaveDetails);
        exporter.export(response);
         
    }

}
