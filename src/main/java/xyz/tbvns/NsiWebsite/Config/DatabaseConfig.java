package xyz.tbvns.NsiWebsite.Config;

import lombok.SneakyThrows;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import xyz.tbvns.NsiWebsite.NsiWebsiteApplication;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Paths;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = "xyz.tbvns.NsiWebsite")
public class DatabaseConfig {

    @Bean
    @SneakyThrows
    public DataSource dataSource() {
        String dbPath = getDatabasePath();
        System.out.println("Database location: " + dbPath);

        // Ensure parent directory exists
        File dbFile = new File(dbPath);
        if (!dbFile.getParentFile().exists()) {
            dbFile.getParentFile().mkdirs();
        }

        org.sqlite.SQLiteConfig config = new org.sqlite.SQLiteConfig();
        config.setEncoding(org.sqlite.SQLiteConfig.Encoding.UTF8);

        return new org.apache.commons.dbcp2.BasicDataSource() {{
            setDriverClassName("org.sqlite.JDBC");
            setUrl("jdbc:sqlite:" + dbPath);
            setUsername("");
            setPassword("");
        }};
    }

    private String getDatabasePath() {
        ApplicationHome home = new ApplicationHome(NsiWebsiteApplication.class);
        File jarFile = home.getSource();

        if (jarFile != null && isRunningFromJar()) {
            return jarFile.getParentFile().getAbsolutePath() + File.separator + "database.db";
        }

        // Running in IDE or tests
        return Paths.get("").toAbsolutePath() + File.separator + "database.db";
    }

    private boolean isRunningFromJar() {
        try {
            return NsiWebsiteApplication.class.getResource("").getProtocol().equals("jar");
        } catch (Exception e) {
            return false;
        }
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("xyz.tbvns.NsiWebsite");
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect");
        properties.setProperty("hibernate.show_sql", "true");
        return properties;
    }
}