package com.santa.work;

import com.github.javafaker.Faker;
import com.santa.work.entity.SecretSantaGroup;
import com.santa.work.entity.Users;
import com.santa.work.enumeration.Role;
import com.santa.work.repository.SecretSantaGroupRepository;
import com.santa.work.repository.UserRepository;
import com.santa.work.service.secretSantaGroup.SecretSantaGroupServiceImpl;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ImplementationTest {
    @Autowired
    private SecretSantaGroupRepository secretSantaGroupRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SecretSantaGroupServiceImpl secretSantaGroupService;

    private Faker faker = new Faker();

    @RepeatedTest(50)
    public void testIntegration() {
        Users user = new Users();
        user.setFirstname(faker.name().firstName());
        user.setLastname(faker.name().lastName());
        user.setLogin(faker.name().username());
        user.setPassword(faker.internet().password());
        user.setRole(Role.ADMIN);
        userRepository.save(user);
        System.out.println(user.getId());
        SecretSantaGroup group = new SecretSantaGroup();
        group.setName(faker.lorem().word());
        secretSantaGroupRepository.save(group);
        group = secretSantaGroupService.createSecretSantaGroup(group, user.getId());
        System.out.println("Url gen : " + group.getUrl());
        System.out.println("admin : " + group.getAdmin().getRole());
    }


    @Test
    public void testUsersId() {
        Users user = new Users();
        user.setFirstname("Roger");
        userRepository.save(user);
        System.out.println(user.getId());
    }

    @Test
    public void test() {
        SecretSantaGroup group = new SecretSantaGroup();
        group.setName("prout");
        secretSantaGroupRepository.save(group);
    }
}
