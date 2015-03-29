
package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import play.Logger;
import play.api.libs.Codecs;
import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import util.PasswordUtils;


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
    
    @Column(columnDefinition="BINARY(20)")
    public byte[] passwordEncrypted;
    
    @Column(columnDefinition="BINARY(16)")
    public byte[] salt;
    
    public static Finder<Long,User> find = new Finder(Long.class, User.class);
    

	public boolean checkPassword(String unencryptedPassword) {
		// TODO Comment the below block when the  new password encryption strategy to use 
		// return password.equals(Codec.hexMD5(unencryptedPassword));
		
		// TODO Open the below block when the new password encryption strategy to use 
		boolean isAuthenticated = false;
//		Logger.info("password -> "+ password + " ; unencryptedPassword => " + unencryptedPassword);
//		Logger.info("passwordEncrypted -> "+ passwordEncrypted );
		if (this.passwordEncrypted == null) {
			if (this.password != null && unencryptedPassword != null) {
				isAuthenticated = this.password.equals(unencryptedPassword);
			}
			Logger.info("isAuthenticated -> "+ isAuthenticated );
			if (isAuthenticated) {
				// If the user is authenticated with the old encrypted password, generate new password
				try {
					setNewPassword(unencryptedPassword);
					this.password = null;
					this.save();
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
			return isAuthenticated;
		} else {
			return PasswordUtils.authenticate(unencryptedPassword, this.passwordEncrypted, this.salt);
		}
    }
    
    public void setNewPassword(String password) throws Exception {
    	// TODO Comment the below block when the  new password encryption strategy to use 
    	 
    	// TODO Open the below block when the new password encryption strategy to use 
    	// Set User Salt
    	setSalt(PasswordUtils.generateSalt());
    	
    	// Set encrypted    	 
    	byte[] encryptedPwd = null;
    	
    	encryptedPwd = PasswordUtils.getEncryptedPassword(password, this.salt);
    	
    	setPasswordEncrypted(encryptedPwd);
    }
    
    public byte[] getPasswordEncrypted() {
		return passwordEncrypted;
	}

	public void setPasswordEncrypted(byte[] passwordEncrypted) {
		this.passwordEncrypted = passwordEncrypted;
	}

	public byte[] getSalt() {
		return salt;
	}

	public void setSalt(byte[] salt) {
		this.salt = salt;
	}
}
