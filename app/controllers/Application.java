package controllers;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import models.Topic;
import models.User;
import models.UserReport;
import models.UserTopic;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import play.Logger;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.pdf.PDF;
import views.html.*;
public class Application extends Controller {

	public static String filePath = Play.application().configuration().getString("data.file.path");
	
	
	public static Result index() {
		session().remove("connected");
        return ok(login.render("MyVoice"));
    }

	public static Result logout() {
		session().remove("connected");
        return ok(login.render("MyVoice"));
	}
	
	public static Result users() {
		User connecteduser = User.find.byId(new Long(session().get("connected")));
        return ok(users.render(connecteduser));
    }
	
	public static Result users_DT_DP() {
		JSONObject obj = new JSONObject();
		List<User> lsUser = User.find.all();
		try {
			obj.put("aaData", lsUser);
			obj.put("sEcho", 1);
			obj.put("iTotalRecords", lsUser.size());
			obj.put("iTotalDisplayRecords", lsUser.size());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		Logger.info("user list--->" + obj.toString());
		return ok(obj.toString());
	}
	
	public static Result adduser() {
		User connecteduser = User.find.byId(new Long(session().get("connected")));
        return ok(adduser.render(connecteduser));
    }
	
	public static Result saveuser() {
		DynamicForm requestData = Form.form().bindFromRequest();
        String email = requestData.get("email");
    	Logger.info("email : " + email );
    	User user = User.find.where().eq("email", email).findUnique();
    	if (user == null ) {
    		User newUser = new User();
    		newUser.createdDate = new Date();
    		newUser.email = email;
    		newUser.password = requestData.get("password");
    		newUser.firstName = requestData.get("firstName");
    		newUser.lastName = requestData.get("lastName");
    		newUser.schoolName = requestData.get("schoolName");
    		newUser.designation = requestData.get("designation");
    		newUser.isAdmin = requestData.get("isAdmin") != null ? true : false;
    		newUser.save();
    		flash("success", "User saved successfully.");
	  	} else {
	  		flash("error", "Oops, User already exists.");
	  	}
    	
    	return users();
    }
	
	public static Result edituser(Long id) {
		User connecteduser = User.find.byId(new Long(session().get("connected")));
    	User user =User.find.byId(id);
        return ok(edituser.render(connecteduser,user));
    }
	
	public static Result editprofile(Long id) {
		User connecteduser = User.find.byId(new Long(session().get("connected")));
    	User user =User.find.byId(id);
        return ok(editprofile.render(connecteduser,user));
    }
	
	public static Result assignuser(Long id) {
		User connecteduser = User.find.byId(new Long(session().get("connected")));
    	User user =User.find.byId(id);
    	List<Topic> alltopic = Topic.find.all();
        return ok(assignuser.render(connecteduser,user,alltopic));
    }
	
	public static Result removetopic(Long id) {
		User connecteduser = User.find.byId(new Long(session().get("connected")));
    	User user =User.find.byId(id);
    	user.defaultTopic = null;
    	user.save();
    	List<Topic> alltopic = Topic.find.all();
    	flash("success", "Topic removed from User successfully.");
        return ok(assignuser.render(connecteduser,user,alltopic));
    }
	
	public static Result saveusertopic() {
		User connecteduser = User.find.byId(new Long(session().get("connected")));
		DynamicForm requestData = Form.form().bindFromRequest();
    	Long id = Long.parseLong(requestData.get("id"));
    	Logger.info("id : " + id );
    	User user =User.find.byId(id);
    	if (user != null ) {
    		Logger.info("topicId " + requestData.get("topicId") );
    		if (requestData.get("topicId") != null && requestData.get("topicId").trim().length() > 0) {
    			Topic topic = Topic.find.byId(Long.parseLong(requestData.get("topicId")));
	    		user.defaultTopic = topic;
	    		user.save();
	    		flash("success", "Topic assigned to User successfully.");
    		} else {
    			flash("error", "Oops, Please select topic and try again.");
    			return assignuser(connecteduser.id);
    		}
    	}
    	return users();
	}
	
	public static Result updateuser() {
    	DynamicForm requestData = Form.form().bindFromRequest();
    	Long id = Long.parseLong(requestData.get("id"));
    	Logger.info("id : " + id );
    	User user =User.find.byId(id);
    	if (user != null ) {
    		user.password = requestData.get("password");
    		user.firstName = requestData.get("firstName");
    		user.lastName = requestData.get("lastName");
    		user.schoolName = requestData.get("schoolName");
    		user.designation = requestData.get("designation");
    		user.isAdmin = requestData.get("isAdmin") != null ? true : false;
    		user.save();
    		flash("success", "User edited successfully.");
	  	} else {
	  		flash("error", "Oops, User not found.");
	  	}
    	return users();
    }
	
	public static Result updateprofile() {
		User connecteduser = User.find.byId(new Long(session().get("connected")));
    	DynamicForm requestData = Form.form().bindFromRequest();
    	Long id = Long.parseLong(requestData.get("id"));
    	Logger.info("id : " + id );
    	User user =User.find.byId(id);
    	if (user != null ) {
    		user.password = requestData.get("password");
    		user.firstName = requestData.get("firstName");
    		user.lastName = requestData.get("lastName");
    		user.isAdmin = requestData.get("isAdmin") != null ? true : false;
    		user.save();
    		flash("success", "Profile updated successfully.");
	  	} else {
	  		flash("error", "Oops, Profile not found.");
	  	}
    	return dashboard(connecteduser.id);
    }
	
	public static Result deleteuser(Long id, Boolean checkrelation) {
    	Logger.info("id : " + id );
    	Logger.info("checkrelation : " + checkrelation );
    	Map<String, String> msg = new HashMap<String,String>();
    	User user =User.find.byId(id);
    	if (user != null ) {
    		if (checkrelation.booleanValue()) {
    	    	List<UserTopic> topics = UserTopic.find.where().eq("user", id).findList();
    	    	if (topics != null && topics.size() > 0) {
        			msg.put("status", "failed");
        			msg.put("msg", "User has reports, are you sure you want to delete both report and this user?");
    	        	return ok(Json.toJson(msg));
    	    	}
        	} 
    		List<UserTopic> userTopics = UserTopic.find.where().eq("user", id).findList();
    		for(UserTopic usrTopic: userTopics) {
    			usrTopic.delete();
    		}
    		user.delete();
			msg.put("status", "success");
			msg.put("msg", "User deleted successfully.");
    		
	  	} else {
	  		msg.put("status", "error");
			msg.put("msg", "Oops, User not found.");
	  	}
    	return ok(Json.toJson(msg));
    }
	
    public static Result authenticate() {
    	DynamicForm requestData = Form.form().bindFromRequest();
        String email = requestData.get("email");
        String password = requestData.get("password");
    	Logger.info("email : " + email + " ; password: " + password);
    	User user = User.find.where().eq("email",email).findUnique();
    	Logger.info("user : " + user);
    	if (user != null && user.email.equalsIgnoreCase(email) && user.password.equalsIgnoreCase(password) ) {
    		  flash("welcome","Welcome! Enjoy testing your reading and writing skills.");
    		  session("connected", String.valueOf(user.id));
    		  Logger.info("user : " + user);
    		 return redirect(controllers.routes.Application.dashboard(user.id)); 
//    		  return dashboard(user.getId());
    	} else {
    		flash("error", "Oops, you are not authorized.");
    		return ok(login.render("MyVoice"));
    	}
    }
    
    public static Result dashboard(Long id) {
//    	User connecteduser = User.find.byId(new Long(session().get("connected")));
    	User user =User.find.byId(id);
    	User connecteduser = user;
    	Topic topic = user.defaultTopic;
    	return ok(dashboard.render(connecteduser,user, topic));
    }
    
    public static Result help() {
    	User connecteduser = User.find.byId(new Long(session().get("connected")));
    	return ok(help.render(connecteduser));
    }
    
    public static Result topics() {
    	User connecteduser = User.find.byId(new Long(session().get("connected")));
        return ok(topics.render(connecteduser));
    }
    
    public static Result userreports() {
    	User connecteduser = User.find.byId(new Long(session().get("connected")));
        return ok(userreports.render(connecteduser));
    }
    
    
    public static Result addtopic() {
    	User connecteduser = User.find.byId(new Long(session().get("connected")));
        return ok(addtopic.render(connecteduser));
    }
    
    public static Result edittopic(Long id) {
    	Topic topic = Topic.find.byId(id);
    	User connecteduser = User.find.byId(new Long(session().get("connected")));
        return ok(edittopic.render(connecteduser,topic));
    }
    
	public static Result topics_DT_DP() {
		
		JSONObject obj = new JSONObject();
		List<Topic> lsTopic = Topic.find.all();
		try {
			obj.put("aaData", lsTopic);
			obj.put("sEcho", 1);
			obj.put("iTotalRecords", lsTopic.size());
			obj.put("iTotalDisplayRecords", lsTopic.size());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		Logger.info("topic list--->" + obj.toString());
		return ok(obj.toString());
	}
    
	public static Result savetopic() {
    	DynamicForm requestData = Form.form().bindFromRequest();
        String title = requestData.get("title");
    	Logger.info("title : " + title );
    	Topic topic = Topic.find.where().eq("title", title).findUnique();
    	if (topic == null ) {
    		Topic newTopic = new Topic();
    		newTopic.createdDate = new Date();
    		newTopic.title = title;
    		newTopic.topic = requestData.get("topic");
    		newTopic.save();
    		flash("success", "Topic saved successfully.");
	  	} else {
	  		flash("error", "Oops, Topic already exists.");
	  	}
    	return topics();
    }
	
	public static Result updatetopic() {
    	DynamicForm requestData = Form.form().bindFromRequest();
    	Long id = Long.parseLong(requestData.get("id"));
    	Logger.info("id : " + id );
    	Topic topic = Topic.find.byId(id);
    	if (topic != null ) {
    		topic.title = requestData.get("title");
    		topic.topic = requestData.get("topic");
    		topic.save();
    		flash("success", "Topic edited successfully.");
	  	} else {
	  		flash("error", "Oops, Topic not found.");
	  	}
    	return topics();
    }
	
	public static Result deletetopic(Long id, Boolean checkrelation) {
    	Logger.info("id : " + id );
    	Logger.info("checkrelation" + checkrelation );
    	Topic topic = Topic.find.byId(id);
    	Map<String, String> msg = new HashMap<String,String>();
    	if (topic != null ) {
    		if (checkrelation.booleanValue()) {
	    		List<User> users = User.find.where().eq("topic", topic).findList();
	    		if (users != null && users.size() > 0) {
	    			msg.put("status", "failed");
	    			msg.put("msg", "Topic is mapped with User, please delete user reports with this topic and try again later.");
		        	return ok(Json.toJson(msg));
	    		}
	    		
	    		List<UserTopic> userTopics = UserTopic.find.where().eq("topic",id).findList();
	    		if (userTopics != null && userTopics.size() > 0) {
	    			msg.put("status", "error");
	    			msg.put("msg", "Topic has reports, are you sure you want to delete both reports and this topic?");
		        	return ok(Json.toJson(msg));
	    		}
    		}
    		
    		List<User> usrList = User.find.where().eq("defaultTopic", id).findList();
    		for (User usr : usrList) {
    			usr.defaultTopic = null;
    			usr.save();
    		}
    		List<UserTopic> userTopics = UserTopic.find.where().eq("topic",id).findList();
    		for(UserTopic usrTopic: userTopics) {
    			usrTopic.delete();
    		}
    		
    		topic.delete();
			msg.put("status", "success");
			msg.put("msg", "Topic deleted successfully.");
	  	} else {
	  		msg.put("status", "error");
			msg.put("msg", "Oops, Topic not found.");
	  	}
    	return ok(Json.toJson(msg));
    }
	
	public static Result deleteusertopic(Long id) {
    	Logger.info("id : " + id );
    	UserTopic usertopic = UserTopic.find.byId( id);
    	if (usertopic != null ) {
    		usertopic.delete();
    		flash("success", "User Topic deleted successfully.");
	  	} else {
	  		flash("error", "Oops, User Topic not found.");
	  	}
    	return report();
    }
	
    public static Result report() {
    	User connecteduser = User.find.byId(new Long(session().get("connected")));
    	Logger.info("connected user in report-->" + connecteduser);
    	return ok(report.render(connecteduser));
    }
    
    public static Result mapusertopic(Long userId, Long topicId) {
		DynamicForm requestData = Form.form().bindFromRequest();
    	String readTime = requestData.get("readTime");
    	String writeTime = requestData.get("writeTime");
    	String usertopic = requestData.get("userTopic");
    	
    	String readingstart = requestData.get("readingstart");
    	String readingend = requestData.get("readingend");
    	String typingstart = requestData.get("typingstart");
    	String typingend = requestData.get("typingend");
    	
    	String matchCharCount = requestData.get("matchCharCount");
    	String mismatchCharCount = requestData.get("mismatchCharCount");
    	String originalCharCount = requestData.get("originalCharCount");
    	
    	Logger.info("readingstart : " + readingstart );
    	Logger.info("readingend : " + readingend );
    	Logger.info("typingstart : " + typingstart );
    	Logger.info("typingend : " + typingend );
    	
    	Logger.info("matchCharCount : " + matchCharCount );
    	Logger.info("mismatchCharCount : " + mismatchCharCount );
    	Logger.info("originalCharCount : " + originalCharCount );
    	
    	Logger.info("userId : " + userId );
    	UserTopic userTopic = new UserTopic();
    	userTopic.createdDate = new Date();
    	userTopic.readTime = readTime;
    	userTopic.writeTime = writeTime;
    	SimpleDateFormat format =  new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//    	SimpleDateFormat inputformat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
    	try {
			userTopic.readingStart = StringUtils.isNotBlank(readingstart) ? format.parse(readingstart) : null;
			userTopic.readingEnd = StringUtils.isNotBlank(readingend) ? format.parse(readingend) : null;
	    	userTopic.typingStart = StringUtils.isNotBlank(typingstart) ? format.parse(typingstart) : null;
	    	userTopic.typingEnd = StringUtils.isNotBlank(typingend) ? format.parse(typingend) : null;
	    	
	    	
	    	SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss:SS");
	    	timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

	    	Date date1 = timeFormat.parse(readTime);
	    	Date date2 = timeFormat.parse(writeTime);

	    	long sum = date1.getTime() + date2.getTime();
	    	Logger.info("sum time -> "+ sum);
	    	String date3 = timeFormat.format(new Date(sum));
	    	userTopic.totalTime = date3;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	userTopic.matchCount = StringUtils.isNotBlank(matchCharCount) ? new Long(matchCharCount) : 0L;
    	userTopic.misMatchCount = StringUtils.isNotBlank(mismatchCharCount) ? new Long(mismatchCharCount) : 0L;
    	userTopic.totalCount = StringUtils.isNotBlank(originalCharCount) ? new Long(originalCharCount) : 0L;
    	
    	
    	Topic topic = Topic.find.byId(topicId);
    	userTopic.topic = topic;
    	User user = User.find.byId(userId);
    	userTopic.user = user;
    	userTopic.userTopic = usertopic;
    	userTopic.save();
		Map<String, String> msg = new HashMap<String,String>();
		msg.put("status", "success");
		msg.put("msg", "Topic saved, Thanks for participating.");
    	return ok(Json.toJson(msg));
	}
    
    public static Result usertopics_DT_DP(Long userId) {
		List<UserTopic> topics = UserTopic.find.where().eq("user",userId).findList();
		
		JSONObject obj = new JSONObject();
		try {
			obj.put("aaData", topics);
			obj.put("sEcho", 1);
			obj.put("iTotalRecords", topics.size());
			obj.put("iTotalDisplayRecords", topics.size());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		Logger.info("topic list--->" + obj.toString());
		return ok(obj.toString());
	}
    
    public static Result userreports_DT_DP() {
    	JSONObject obj = new JSONObject();
    	try {
    	List<UserTopic> topics = UserTopic.find.where().eq("user.id",new Long(session().get("connected"))).findList();
    	Logger.info("user report->" + topics);
    	List<UserReport> reports = new ArrayList<UserReport>();
    	try {
	    	if (topics != null && topics.size() > 0) {
	    		for(UserTopic usertopic: topics) {
	    			UserReport report = new UserReport();
	    			report.id = usertopic.id;
	    			report.title = usertopic.topic.title;
	    			report.email = usertopic.user.email;
	    			report.readTime = usertopic.readTime;
	    			report.writeTime = usertopic.writeTime;
	    			report.totalTime = usertopic.totalTime;
	    			report.readingStart = usertopic.readingStart;
	    			report.readingEnd = usertopic.readingEnd;
	    			report.typingStart = usertopic.typingStart;
	    			report.typingEnd = usertopic.typingEnd;
	    			
	    			report.matchCount = usertopic.matchCount;
	    			report.misMatchCount = usertopic.misMatchCount;
	    			report.totalCount = usertopic.totalCount;
	    			
	    			report.createdDate = usertopic.createdDate;
	    			report.originalTopic = usertopic.topic.topic;
	    			report.userTopic = usertopic.userTopic;
	    			reports.add(report);
	    		}
	    	}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	Logger.info("reports list--->" + reports);
		
		try {
			obj.put("aaData", reports);
			obj.put("sEcho", 1);
			obj.put("iTotalRecords", reports.size());
			obj.put("iTotalDisplayRecords", reports.size());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
		Logger.info("topic list--->" + obj.toString());
		return ok(obj.toString());
	}
    
    public static Result pdfreport(Long selectId) {
    	User connecteduser = User.find.byId(new Long(session().get("connected")));
    	UserTopic userTopic = UserTopic.find.byId(selectId);
    	BigDecimal perc = new BigDecimal(0);
    	Date currDate = new Date();
    	String currDateStr = null;
    	if (userTopic != null) {
    		Logger.info("userTopic.matchCount--->" + userTopic.matchCount);
    		Logger.info("userTopic.totalCount--->" + userTopic.totalCount);
    		BigDecimal per = new BigDecimal((userTopic.matchCount.doubleValue()/userTopic.totalCount.doubleValue())*100);
    		Logger.info("per--->" + per);
    		perc = per.round(new MathContext(4, RoundingMode.HALF_UP));
    		Logger.info("perc--->" + perc);
    		SimpleDateFormat format =  new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    		currDateStr = format.format(currDate);
			userTopic.readingStartStr = userTopic.readingStart != null ? format.format(userTopic.readingStart) : null;
			userTopic.readingEndStr = userTopic.readingEnd != null ? format.format(userTopic.readingEnd) : null;
	    	userTopic.typingStartStr = userTopic.typingStart != null ? format.format(userTopic.typingStart) : null;
	    	userTopic.typingEndStr = userTopic.typingEnd != null ? format.format(userTopic.typingEnd) : null;
    	}
    	return PDF.ok(pdfreport.render(userTopic,perc.doubleValue(),currDateStr));
    }
    
    public static Result viewuserreport(Long selectedId) {
    	User connecteduser = User.find.byId(new Long(session().get("connected")));
    	UserTopic userTopic = UserTopic.find.byId(selectedId);
    	BigDecimal perc = new BigDecimal(0);
    	Date currDate = new Date();
    	String currDateStr = null;
    	if (userTopic != null) {
    		Logger.info("userTopic.matchCount--->" + userTopic.matchCount);
    		Logger.info("userTopic.totalCount--->" + userTopic.totalCount);
    		BigDecimal per = new BigDecimal((userTopic.matchCount.doubleValue()/userTopic.totalCount.doubleValue())*100);
    		Logger.info("per--->" + per);
    		perc = per.round(new MathContext(4, RoundingMode.HALF_UP));
    		Logger.info("perc--->" + perc);
    		SimpleDateFormat format =  new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    		currDateStr = format.format(currDate);
			userTopic.readingStartStr = userTopic.readingStart != null ? format.format(userTopic.readingStart) : null;
			userTopic.readingEndStr = userTopic.readingEnd != null ? format.format(userTopic.readingEnd) : null;
	    	userTopic.typingStartStr = userTopic.typingStart != null ? format.format(userTopic.typingStart) : null;
	    	userTopic.typingEndStr = userTopic.typingEnd != null ? format.format(userTopic.typingEnd) : null;
    	}
    	
    	return ok(viewuserreport.render(userTopic,perc.doubleValue(),currDateStr));
    }
}
