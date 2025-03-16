package xyz.tbvns.NsiWebsite.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO implements Serializable {
	private String username;
	private String password;

	@Override
 	public String toString(){
		return 
			"LoginDTO{" +
			"username = '" + username + '\'' + 
			",password = '" + password + '\'' +
			"}";
		}
}