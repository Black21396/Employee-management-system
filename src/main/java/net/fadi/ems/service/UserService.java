package net.fadi.ems.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import net.fadi.ems.entity.User;
import net.fadi.ems.repository.UserRepository;
import net.fadi.ems.service.interfaces.UserServiceInterface;

@Service
@AllArgsConstructor
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
