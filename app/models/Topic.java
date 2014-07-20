package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

/**
 * @author ubensti
 *
 */
@Entity
public class Topic extends Model{
	@Id
	public Long id;
	@Required
	public String title;
	@Formats.DateTime(pattern="dd/MM/yyyy")
	public Date createdDate;
	@Lob
	public String topic;
	public Boolean deleted = false;
	public static Finder<Long,Topic> find = new Finder(Long.class, Topic.class);
}
