package com.avis;


	import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
	import org.springframework.security.crypto.password.PasswordEncoder;

	public class PasswordTest {
	    public static void main(String[] args) {
	        PasswordEncoder encoder = new BCryptPasswordEncoder();
	     // Encode a password
	        String rawPassword = "123"; // Password used during registration
	        String encodedPassword = encoder.encode(rawPassword);
	        System.out.println("Encoded Password: " + encodedPassword);
	        
	        // Check if the raw password matches the encoded password
	        String passwordToCheck = "123"; // Use the same raw password for checking
	        boolean matches = encoder.matches(passwordToCheck, encodedPassword);
	        System.out.println("Password matches: " + matches);
	        
	        // Example of a different password
	        String wrongPassword = "12";
	        boolean wrongPasswordMatches = encoder.matches(wrongPassword, encodedPassword);
	        System.out.println("Wrong password matches: " + wrongPasswordMatches);
}
}
	
	