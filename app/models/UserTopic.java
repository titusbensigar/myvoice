package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import play.data.format.Formats;
import play.db.ebean.Model;


/**
 * @author ubensti
 *
 */
@Entity
public class UserTopic  extends Model{
	@Id
	public Long id;
	@ManyToOne
	public User user;
	@ManyToOne
	public Topic topic;
	@Lob
	public String userTopic;
	@Formats.DateTime(pattern="dd/MM/yyyy")
	public Date createdDate;
	public String readTime;
	public String writeTime;
	
	public String totalTime;
	
	public Date readingStart;
	public Date readingEnd;
	public Date typingStart;
	public Date typingEnd;
	
	public Long matchCount;
	public Long misMatchCount;
	public Long totalCount;
	
	public Boolean deleted = false;
	public static Finder<Long,UserTopic> find = new Finder(Long.class, UserTopic.class);
	
	public String filename;
	
	@Transient
	public String readingStartStr;
	@Transient
	public String readingEndStr;
	@Transient
	public String typingStartStr;
	@Transient
	public String typingEndStr;
}
