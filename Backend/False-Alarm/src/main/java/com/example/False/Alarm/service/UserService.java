package com.example.False.Alarm.service;

import com.example.False.Alarm.dto.AddUserRequest;
import com.example.False.Alarm.dto.UserSearchDTO;
import com.example.False.Alarm.enums.UserType;
import com.example.False.Alarm.mapper.UserMapper;
import com.example.False.Alarm.model.User;
import com.example.False.Alarm.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;


    public ResponseEntity<?> addUser(String path, AddUserRequest addUserRequest) throws IOException {
        MultipartFile image = addUserRequest.getImage();
        String response = uploadImage(path,image);
        if(response.equals("The uploaded file format is not supported")){
            return new ResponseEntity<>("The uploaded file format is not supported", HttpStatus.BAD_REQUEST);
        }

        User user = UserMapper.mapToUser(addUserRequest);
        user.setProfilePicUrl(response);
        user.setUserType(UserType.USER);
        user.setAuthorities("USER");
        userRepository.save(user);
        log.info("User added successfully");
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    public ResponseEntity<?> addAdmin(String path, AddUserRequest addUserRequest) throws IOException {
        MultipartFile image = addUserRequest.getImage();
        String response = uploadImage(path,image);
        if(response.equals("The uploaded file format is not supported")){
            return new ResponseEntity<>("The uploaded file format is not supported", HttpStatus.BAD_REQUEST);
        }

        User admin = UserMapper.mapToUser(addUserRequest);
        admin.setProfilePicUrl(response);
        admin.setUserType(UserType.ADMIN);
        admin.setAuthorities("ADMIN");
        userRepository.save(admin);
        log.info("Admin added by admin successfully");
        return new ResponseEntity<>(admin, HttpStatus.CREATED);
    }

    private String uploadImage(String path, MultipartFile image) throws IOException {
        String fileName = image.getOriginalFilename();

        String extension = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
        if(!extension.equals("png" ) && !extension.equals("jpg") && !extension.equals("jpeg")){
            log.info("File extension not supported");
            return "The uploaded file format is not supported";
        }

        String randomID = UUID.randomUUID().toString();
        String fileNameWhenStored = randomID.concat(fileName.substring(fileName.lastIndexOf(".")));
        log.info("File name when stored is {}", fileNameWhenStored);
        String filePath = path + File.separator + fileNameWhenStored ;
        log.info("File name saved: "+filePath);
        File dest = new File(path);
        if(!dest.exists()){
            dest.mkdirs();
        }

        Files.copy(image.getInputStream(), Paths.get(filePath));
        log.info("Image Uploaded Successfully");
        return fileNameWhenStored;
    }



    public List<User> searchByUsername(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }

    public List<User> searchByUserId(String query) {
        return userRepository.findByUserIdContainingIgnoreCase(query);
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId);

        if(user != null){
            return user;

        }
        throw new  UsernameNotFoundException(userId.concat(" doesnot exist"));

    }
}
