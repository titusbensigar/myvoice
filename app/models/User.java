
package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class User extends  Model{

	@Id
	public Long id;
	@Required
    public String email;
	@Required
    public String password;
    public String firstName;
    public String lastName;
    @Formats.DateTime(pattern="dd/MM/yyyy")
    public Date createdDate;
    public Boolean isAdmin;
    public Boolean deleted = false;
    @OneToOne
    public Topic defaultTopic;
    
    public String schoolName;
    public String designation;
    public static Finder<Long,User> find = new Finder(Long.class, User.class);
}
