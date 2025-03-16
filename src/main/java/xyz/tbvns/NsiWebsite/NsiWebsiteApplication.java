package xyz.tbvns.NsiWebsite;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import xyz.tbvns.EZConfig;
import xyz.tbvns.NsiWebsite.Config.WebConf;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class NsiWebsiteApplication {

	@SneakyThrows
	public static void main(String[] args) {
		EZConfig.registerClassPath("xyz.tbvns.NsiWebsite.Config");
		EZConfig.setConfigFolder(new ApplicationHome(NsiWebsiteApplication.class).getDir().getPath() + "/Config");
		EZConfig.load();
		EZConfig.save();

		// Create properties map with configuration values
		Map<String, Object> properties = new HashMap<>();
		properties.put("server.host", WebConf.host);
		properties.put("server.port", WebConf.port);

		// Configure and run Spring Application
		SpringApplication app = new SpringApplication(NsiWebsiteApplication.class);
		app.setDefaultProperties(properties);
		app.run(args);
	}
}