package alignWebsite.alignadmin;

import java.util.List;

import org.hibernate.HibernateException;
import org.junit.*;
import org.mehaexample.asdDemo.dao.alignadmin.AdministratorsDao;
import org.mehaexample.asdDemo.model.alignadmin.Administrators;

public class AdministratorsDaoTest {

	private static AdministratorsDao administratorsDao;

	@BeforeClass
	public static void init() {
		administratorsDao = new AdministratorsDao(true);
	}

	@Before
	public void addDatabase() {
		Administrators newAdministrator = new Administrators("987654321", "catwoman@gmail.com",
						"Cat", "Main", "Woman");
		Administrators administrators = administratorsDao.addAdministrator(newAdministrator);
	}

	@After
	public void deleteDatabase() {
		administratorsDao.deleteAdministrator("987654321");
	}

	/**
	 * This is test for illegal argument of adding new Administrator.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void addNullAdministratorTest() {
		administratorsDao.addAdministrator(null);
	}

	/**
	 * This is test for adding duplicate Administrator.
	 */
	@Test(expected = HibernateException.class)
	public void addDuplicateAdminTest() {
		Administrators newAdministrator = new Administrators("987654321", "catwoman@gmail.com",
						"Cat", "Main", "Woman");
		administratorsDao.addAdministrator(newAdministrator);
	}

	/**
	 * This is test for updating non existent Administrator.
	 */
	@Test(expected = HibernateException.class)
	public void updateNonExistentAdminTest() {
		Administrators newAdministrator = new Administrators("000011111", "cchent@gmail.com",
						"Tai", "Main", "Chen");
		administratorsDao.updateAdministratorRecord(newAdministrator);
	}

	/**
	 * This is test for deleting non existent Administrator.
	 */
	@Test(expected = HibernateException.class)
	public void deleteNonExistentAdminTest() {
		administratorsDao.deleteAdministrator("00001111");
	}

	/**
	 * This is test for adding new Administrator record.
	 */
	@Test
	public void addAdministratorTest() {
		Administrators newAdministrator = new Administrators("123789456", "john.stewart@gmail.com", 
				"John", "Main", "Stewart");
		Administrators administrators = administratorsDao.addAdministrator(newAdministrator);
		Assert.assertTrue(administrators.toString().equals(newAdministrator.toString()));
		administratorsDao.deleteAdministrator("123789456");
	}

	/**
	 * This is test for deleting Administrator record.
	 */
	@Test
	public void deleteAdminRecordTest() {
		List<Administrators> administrators = administratorsDao.getAllAdminstrators();
		int oldSize = administrators.size();		
		Administrators newAdministrator = new Administrators("123789456", "john.stewart@gmail.com", 
				"John", "Main", "Stewart");		

		administratorsDao.addAdministrator(newAdministrator);
		administratorsDao.deleteAdministrator("123789456");
		int newSize = administrators.size();	
		Assert.assertEquals(oldSize, newSize); 
	}

	/**
	 * This is test for retrieving all Administrator records.
	 */
	@Test
	public void getAllAdministratorsTest() {
		List<Administrators> administrators = administratorsDao.getAllAdminstrators();

		Administrators newAdministrator = new Administrators("111", "john.lstew1art@gmail.com", 
				"John", "Main", "Stewart");	
		administratorsDao.addAdministrator(newAdministrator);
		List<Administrators> newAdministrators = administratorsDao.getAllAdminstrators();
		Assert.assertTrue(newAdministrators.size() == administrators.size() + 1);
		administratorsDao.deleteAdministrator("111");
	}

	/**
	 * This is test for getting Administrator by neu id.
	 */
	@Test
	public void getAdminRecordTest() {
		Administrators newAdministrator = new Administrators("123789456", "john.stewart@gmail.com", 
				"John", "Main", "Stewart");	

		administratorsDao.addAdministrator(newAdministrator);
		Administrators administrator = administratorsDao.getAdministratorRecord("123789456");
		Assert.assertTrue(administrator.toString().equals(newAdministrator.toString()));
		administratorsDao.deleteAdministrator("123789456");

	}

	/**
	 * This is test for searching for Administrator by email address.
	 */
	@Test
	public void findAdministratorByEmail() {
		Administrators newAdministrator = new Administrators("123789456", "john.stewart@gmail.com",
						"John", "Main", "Stewart");

		administratorsDao.addAdministrator(newAdministrator);
		Administrators administrator = administratorsDao.findAdministratorByEmail("john.stewart@gmail.com");
		Assert.assertTrue(administrator.toString().equals(newAdministrator.toString()));
		Assert.assertTrue(administratorsDao.findAdministratorByEmail("test@gmail.com") == null);
		administratorsDao.deleteAdministrator("123789456");
	}

	/**
	 * This is test for updating Administrator record.
	 */
	@Test
	public void updateAdministratorRecordTest() {
		Administrators administrator = new Administrators("123789456", "john.stewart@gmail.com", 
				"John", "Main", "Stewart");	

		administratorsDao.addAdministrator(administrator);

		administrator = administratorsDao.getAdministratorRecord("123789456");
		Assert.assertTrue(administrator.getEmail().equals("john.stewart@gmail.com"));

		Administrators newAdministrator = new Administrators("123789456", "john.stewart123@gmail.com", 
				"John", "Main", "Stewart");

		administratorsDao.updateAdministratorRecord(newAdministrator);
		Assert.assertTrue(administratorsDao.getAdministratorRecord("123789456")
				.getEmail().equals("john.stewart123@gmail.com"));

		administratorsDao.deleteAdministrator("123789456");
	}

	/**
	 * This is test for deleting Administrator record.
	 */
	@Test
	public void deleteNullAdministratorTest() {
		boolean result = administratorsDao.deleteAdministrator("");
		Assert.assertFalse(result);

		result = administratorsDao.deleteAdministrator(null);
		Assert.assertFalse(result);
	}
}
