package ie.revenue.rccourse.SpringJdbcTest;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import ie.rccourse.userDb.SpringUserDb;
import ie.rccourse.userDb.UserDbException;
import ie.rccourse.userDb.User;
import ie.rccourse.userDb.UserDb;
import ie.rccourse.userDb.UserTransaction;

public class SpringUserDbTest {
	
	UserDb userDb;
	
	@Before
	public void setup() throws UserDbException {
		
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"SpringBeans.xml");
					    	
    	DriverManagerDataSource dmds = context.getBean(DriverManagerDataSource.class);
    	
    	userDb = new SpringUserDb(dmds);
    	//userDb = context.getBean(SpringUserDb.class);
    	
    	User user = new User (-1, "Tester", "MCTESTFACE", true, "2000-01-01");
    	
    	userDb.create(user);
    	
    	UserTransaction ut = new UserTransaction(-1, user.getId(), "Transaction for Testing", "2016-01-01", 100.00);
    	userDb.createTransaction(ut);
    	
    	ut = new UserTransaction(-1, user.getId(), "Second Transaction for Testing", "2016-01-01", -50.00);
    	userDb.createTransaction(ut);
    	
    	//create 10 test users
	}
	
	@After
	public void tearDown() {
		
		//delete the remaining 9 test users
		
		userDb.close();
	}

	@Test
	public void testGetUser() throws UserDbException {
		
		List<User> users = userDb.getUsers();
		
		User userToFind = users.get(0);
		User user;
		
		user = userDb.getUser(userToFind.getId());
		
		assertTrue(user.equals(userToFind));
		
	}
	
	@Test
	public void testCreate() throws UserDbException {
		
		User user = new User(-1, "Create1", "Test", false, "2000-01-01");
		
		userDb.create(user);
		
		int id = user.getId();
		
		User check = userDb.getUser(id);
		
		assertTrue(user.equals(check));
	}
	
	@Test
	public void testDelete() {
		
		List<User> users = userDb.getUsers();
		
		User userToDelete = users.get(0);
		
		int beforeCount = users.size();
		
		userDb.delete(userToDelete.getId());
		
		users = userDb.getUsers();
		
		int afterCount = users.size();
		
		assertTrue(afterCount == beforeCount - 1);
		
	}
	
	
	@Test
	public void testDeleteWithTransactions() throws UserDbException {
		
		User user = new User(-1, "TESTER", "MCTESTFACE", false, "2000-01-01");
		userDb.create(user);
		
		UserTransaction tx = new UserTransaction(-1, user.getId(), "TEST TX", "2000-01-01", 99.0);
		userDb.createTransaction(tx);
		
		userDb.delete(user.getId());
		
		List<UserTransaction>txs = userDb.getTransactionForUser(user.getId());
		assertTrue(txs.size() == 0);
	}
	
	@Test
	public void testSearch() throws UserDbException{
		
		User user = new User(-1, "someXXXthing", "someXXXthing", false, "2001-01-01");
		
		userDb.create(user);
		
		List<User> results = userDb.find("XXX");
		
		assertTrue(results.size()>0);
	}

	
}
