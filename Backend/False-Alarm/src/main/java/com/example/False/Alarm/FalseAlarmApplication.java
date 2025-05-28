package com.example.False.Alarm;

import com.example.False.Alarm.enums.UserType;
import com.example.False.Alarm.model.User;
import com.example.False.Alarm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FalseAlarmApplication implements CommandLineRunner {

	@Autowired
	public UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(FalseAlarmApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if(userRepository.findByUserId("admin") == null) {
			User user = User.builder().username("admin").password("admin").userId("admin").userType(UserType.ADMIN).email("admin@gmail.com").authorities("ADMIN").build();
			userRepository.save(user);
		}

	}
}
