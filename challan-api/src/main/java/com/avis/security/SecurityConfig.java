package com.avis.security;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



	@Configuration
	@EnableWebSecurity
	public class SecurityConfig {

	    private final UserDetailsService userDetailsService;

	    public SecurityConfig(UserDetailsService userDetailsService) {
	        this.userDetailsService = userDetailsService;
	    }

	    @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    	 http
	            .authorizeRequests()
	                .antMatchers("/register", "/login", "/index", "/home", "/css/**", "/js/**", "/error").permitAll()
	                .antMatchers("/rcindex", "/uploadExcel", "/getAll").authenticated()  // Require authentication
	                .antMatchers("/change-password").authenticated()
	                .anyRequest().authenticated()
	            .and()
	            .formLogin()
	                .loginPage("/login")
	                .defaultSuccessUrl("/index", true)  // Redirect to rchome after login
	                .permitAll()
	            .and()
	            .logout()
	                .logoutUrl("/logout")
	                .logoutSuccessUrl("/login?logout")
	               // .invalidateHttpSession(true)  // Invalidate session after logout
	               // .deleteCookies("JSESSIONID")
	                .permitAll()
	            .and()
	            .csrf().disable() // If you want to enable CSRF, use .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
	            .headers()
	                .frameOptions().disable();
	        
	        return http.build();
	    }

	    
	    
	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }

	    
	}


