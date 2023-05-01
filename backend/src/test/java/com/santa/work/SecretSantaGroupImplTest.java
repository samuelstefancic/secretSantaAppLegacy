package com.santa.work;

import com.santa.work.entity.SecretSantaGroup;
import com.santa.work.entity.Users;
import com.santa.work.enumeration.Role;
import com.santa.work.repository.SecretSantaGroupRepository;
import com.santa.work.repository.UserRepository;
import com.santa.work.service.secretSantaGroup.SecretSantaGroupServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;


@SpringBootTest
@Transactional
public class SecretSantaGroupImplTest {
    @SpyBean
    private SecretSantaGroupRepository secretSantaGroupRepository;
    @SpyBean
    private UserRepository userRepository;
    @InjectMocks
    private SecretSantaGroupServiceImpl secretSantaGroupService;


    @Test
    public void testCreateSecretSantaGroup() {

        Users adminUser = new Users();
        adminUser.setRole(Role.ADMIN);
        userRepository.save(adminUser);

        SecretSantaGroup secretSantaGroup = new SecretSantaGroup();
        secretSantaGroup.setName("The best santa group");
        secretSantaGroupRepository.save(secretSantaGroup);
        //Prepare data for tests
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));

        SecretSantaGroup createdGroup = secretSantaGroupService.createSecretSantaGroup(secretSantaGroup, adminUser.getId());

        System.out.println("Url gen " + createdGroup.getUrl());
        System.out.println("admin : " + createdGroup.getAdmin().getRole());

        verify(userRepository, times(1)).findById(adminUser.getId());
        verify(secretSantaGroupRepository, times(1)).save(any(SecretSantaGroup.class));
    }

    /*
    @Test
    public void testCreateSecretSantaGroup2() {
        Users adminUser = new Users();
        adminUser.setRole(Role.ADMIN);

        secretSantaGroup = new SecretSantaGroup();
        secretSantaGroup.setName("The best santa group");

        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(secretSantaGroupRepository.save(any(SecretSantaGroup.class))).thenReturn(secretSantaGroup);

        SecretSantaGroup createdGroup = secretSantaGroupService.createSecretSantaGroup(secretSantaGroup, adminUser.getId());

        System.out.println("Url gen " + createdGroup.getUrl());
        System.out.println("admin : " + createdGroup.getAdmin().getRole());

        verify(userRepository, times(1)).findById(adminUser.getId());
        verify(secretSantaGroupRepository, times(1)).save(any(SecretSantaGroup.class));
    }
*/
}
