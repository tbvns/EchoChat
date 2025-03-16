package xyz.tbvns.NsiWebsite.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO implements Serializable {
	private boolean success;
	private String username;
	private String email;
	private String token;

	@Override
	public String toString() {
		return "LoginResponseDTO{" +
				"success=" + success +
				", username='" + username + '\'' +
				", email='" + email + '\'' +
				", token='" + token + '\'' +
				'}';
	}
}