package com.trial.dvoc.service;

import com.trial.dvoc.model.Coupon;
import com.trial.dvoc.model.User;
import com.trial.dvoc.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public void register(User user){
        if(repo.existsByEmail(
                        user.getEmail()
                )){
            throw new RuntimeException(
                    "Email exists"
            );
        }

        repo.save(user);
    }

    public User login( String email,String password ){
        User user= repo.findByEmail(email);

        if( user!=null && user.getPassword().equals(password) ){
            return user;
        }
        return null;
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }
    
    public void addPoints(Long userId,int pts){
        User user = repo.findById(userId).orElse(null);
    
        if(user!=null){
            user.setPoints(user.getPoints()+pts);
            repo.saveAndFlush(user);
        }
    }

    public User findById(Long id){
     return repo.findById(id).orElse(null);
    }

}
