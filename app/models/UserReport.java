/**
 * 
 */
package models;

import java.util.Date;


/**
 * @author ubensti
 *
 */

public class UserReport  {
	
	public Long id;
	public String email;
	public String readTime;
	public String writeTime;
	public String totalTime;
	public Date createdDate;
	public String title;
	public String originalTopic;
	public String userTopic;
	
	public Date readingStart;
	public Date readingEnd;
	public Date typingStart;
	public Date typingEnd;
	
	public Long matchCount;
	public Long misMatchCount;
	public Long totalCount;
	
//	public boolean status;
//	public int noOfErrors;
//	public String errors;
	
}
