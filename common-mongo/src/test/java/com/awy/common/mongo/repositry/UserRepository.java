package com.awy.common.mongo.repositry;

import org.springframework.data.domain.*;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.stream.Collectors;

public interface UserRepository extends MongoRepository<User,Integer> {

    public static void main(String[] args) {
        UserRepository userRepository = null;

        Sort sort = Sort.by(Sort.Direction.DESC, "");
        Pageable pageable = PageRequest.of(1,10,sort);
        User user = new User();

        Page<User> all = userRepository.findAll(Example.of(user), pageable);

        all.getContent();
        all.get().collect(Collectors.toList());



//        User user = new User();
        //去首部空格
        user.toString().stripLeading();
        //去尾部空格
        user.toString().stripTrailing();
    }
}
