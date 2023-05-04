package com.santa.work;

import com.santa.work.entity.Invitation;
import com.santa.work.entity.SecretSantaGroup;
import com.santa.work.entity.Users;
import com.santa.work.entity.Wish;
import com.santa.work.enumeration.InvitationStatus;
import com.santa.work.enumeration.Role;
import com.santa.work.repository.SecretSantaGroupRepository;
import com.santa.work.repository.UserRepository;
import com.santa.work.service.invitation.InvitationServiceImpl;
import com.santa.work.service.secretSantaGroup.SecretSantaGroupServiceImpl;
import com.santa.work.service.wish.WishServiceImpl;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class WorkApplicationTests {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SecretSantaGroupRepository secretSantaGroupRepository;
	@Autowired
	private SecretSantaGroupServiceImpl secretSantaGroupService;
	@Autowired
	private WishServiceImpl wishService;
	@Autowired
	private InvitationServiceImpl invitationService;

    /*WorkApplicationTests(SecretSantaGroupRepository secretSantaGroupRepository, SecretSantaGroupServiceImpl secretSantaGroupService) {
		this.secretSantaGroupRepository = secretSantaGroupRepository;
		this.secretSantaGroupService = secretSantaGroupService;
	}*/

	@Test
	void contextLoads() {
	}

	@Test
	public void testCreateUserAndSecretGroup() {
		Users user = new Users();
		user.setFirstname("John");
		user.setLastname("Doe");
		user.setLogin("johndoe");
		user.setPassword("password");
		user.setRole(Role.ADMIN);

		user = userRepository.save(user);

		SecretSantaGroup group = new SecretSantaGroup();
		group.setName("Group testing");
		secretSantaGroupRepository.save(group);
		System.out.println(group.getId());

		SecretSantaGroup createdGroup = secretSantaGroupService.createSecretSantaGroup(group, user.getId());

		assertEquals(1, user.getGroups().size());
		assertEquals(1, createdGroup.getMembers().size());
		assertEquals(user, createdGroup.getMembers().get(0));
	}

	@Test
	public void testCreateInvitationAndAddToSecretSantaGroup() {
		Invitation invitation = new Invitation();
		invitation.setEmail("test@email.com");
		invitation.setToken(invitationService.generateUniqueToken());
		invitation.setStatus(InvitationStatus.PENDING);
		invitation.setExpiryDate(LocalDateTime.now().plusDays(7));

		SecretSantaGroup group = new SecretSantaGroup();
		group.setName("Test invitation group");

		group.addInvitation(invitation);
		assertEquals(1, group.getInvitations().size());
		assertEquals(invitation, group.getInvitations().get(0));
	}




    @Test
	public void testCreateWishAndAddToUser() {

		//Creating wish

		Wish wish = new Wish();
		wish.setTitle("Wish test");
		wish.setDescription("description wish test");
		wish.setUrl("http://exampleWishUrl.com/test-Wish/with-test");
		//wish = wishService.createWish(wish);

		//Creating user

		Users user = new Users();
		user.setFirstname("John");
		user.setLastname("Doe");
		user.setLogin("johndoe");
		user.setPassword("password");

		//Associate the wish to a list and use the list to attribute the list to the user wish list which is a list (pfhew)

		List<Wish> wishList = new ArrayList<>();
		wishList.add(wish);
		user.setWishList(wishList);
		userRepository.save(user);

		//Test the list

		Users updatedUserTest = userRepository.findById(user.getId()).orElse(null);
		assertNotNull(updatedUserTest);
		assertEquals(1, updatedUserTest.getWishList().size());
		assertEquals(wish, updatedUserTest.getWishList().get(0));
	}
}
