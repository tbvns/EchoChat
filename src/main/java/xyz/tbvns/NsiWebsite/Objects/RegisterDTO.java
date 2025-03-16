package xyz.tbvns.NsiWebsite.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO implements Serializable {
	private String username;
	private String email;
	private String password;

	@Override
 	public String toString(){
		return 
			"RegisterDTO{" +
			"username = '" + username + '\'' + 
			",email = '" + email + '\'' + 
			",password = '" + password + '\'' + 
			"}";
		}
}