package alignWebsite.alignprivate;

import org.hibernate.HibernateException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mehaexample.asdDemo.dao.alignprivate.PrivaciesDao;
import org.mehaexample.asdDemo.dao.alignprivate.StudentsDao;
import org.mehaexample.asdDemo.dao.alignprivate.WorkExperiencesDao;
import org.mehaexample.asdDemo.dao.alignpublic.MultipleValueAggregatedDataDao;
import org.mehaexample.asdDemo.enums.Campus;
import org.mehaexample.asdDemo.enums.DegreeCandidacy;
import org.mehaexample.asdDemo.enums.EnrollmentStatus;
import org.mehaexample.asdDemo.enums.Gender;
import org.mehaexample.asdDemo.enums.Term;
import org.mehaexample.asdDemo.model.alignadmin.CompanyRatio;
import org.mehaexample.asdDemo.model.alignadmin.TopEmployer;
import org.mehaexample.asdDemo.model.alignprivate.*;
import org.mehaexample.asdDemo.model.alignpublic.MultipleValueAggregatedData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class WorkExperiencesDaoTest {
  private static WorkExperiencesDao workExperiencesDao;
  private static StudentsDao studentsDao;
  private static PrivaciesDao privaciesDao;

  @BeforeClass
  public static void init() {
    workExperiencesDao = new WorkExperiencesDao(true);
    studentsDao = new StudentsDao(true);
    privaciesDao = new PrivaciesDao(true);

//    workExperiencesDao = new WorkExperiencesDao();
//    studentsDao = new StudentsDao();
//    privaciesDao = new PrivaciesDao();
  }

  @Before
  public void addDatabasePlaceholder() throws ParseException {
    Students student = new Students("001234567","tomcat@gmail.com", "Tom", "",
            "Cat", Gender.M, "F1", "1111111111",
            "401 Terry Ave", "WA", "Seattle", "98109",
            Term.FALL, 2014, Term.SPRING, 2016,
            EnrollmentStatus.FULL_TIME, Campus.SEATTLE, DegreeCandidacy.MASTERS,null, true);
    Students student2 = new Students("111234567","jerrymouse@gmail.com", "Jerry", "",
            "Mouse", Gender.M, "F1", "1111111111",
            "401 Terry Ave", "WA", "Seattle", "98109",
            Term.FALL, 2014, Term.SPRING, 2016,
            EnrollmentStatus.FULL_TIME, Campus.SEATTLE, DegreeCandidacy.MASTERS,null, true);
    studentsDao.addStudent(student);
    studentsDao.addStudent(student2);

    Privacies privacy = new Privacies();
    privacy.setNeuId("001234567");
    privacy.setPublicId(studentsDao.getStudentRecord("001234567").getPublicId());
    privacy.setCoop(true);
    privaciesDao.createPrivacy(privacy);

    WorkExperiences newWorkExperience = new WorkExperiences();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    newWorkExperience.setStartDate(dateFormat.parse("2017-06-01"));
    newWorkExperience.setEndDate(dateFormat.parse("2017-12-01"));
    newWorkExperience.setCurrentJob(false);
    newWorkExperience.setCoop(true);
    newWorkExperience.setTitle("Title");
    newWorkExperience.setDescription("Description");
    newWorkExperience.setNeuId(student.getNeuId());
    newWorkExperience.setCompanyName("Amazon");
    workExperiencesDao.createWorkExperience(newWorkExperience);
  }

  @After
  public void deleteDatabasePlaceholder() {
    workExperiencesDao.deleteWorkExperienceByNeuId("001234567");
    workExperiencesDao.deleteWorkExperienceByNeuId("111234567");
    studentsDao.deleteStudent("001234567");
    studentsDao.deleteStudent("111234567");
  }

  /**
   * This is test for deleting non existent work experience
   */
  @Test(expected = HibernateException.class)
  public void deleteNonExistWorkExperience() {
    workExperiencesDao.deleteWorkExperienceById(-200);
  }

  /**
   * This is test for updating non existent work experience
   */
  @Test(expected = HibernateException.class)
  public void updateNonExistWorkExperience() {
    WorkExperiences newWorkExperience = new WorkExperiences();
    newWorkExperience.setWorkExperienceId(-300);
    workExperiencesDao.updateWorkExperience(newWorkExperience);
  }

  /**
   * Testing for getting list of students working in the specific company.
   * @throws ParseException
   */
  @Test
  public void getStudentsWorkingInACompanyTest() throws ParseException {
    WorkExperiences newWorkExperience = new WorkExperiences();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    newWorkExperience.setStartDate(dateFormat.parse("2018-06-01"));
    newWorkExperience.setEndDate(dateFormat.parse("2018-12-01"));
    newWorkExperience.setCurrentJob(false);
    newWorkExperience.setCoop(false);
    newWorkExperience.setTitle("Title");
    newWorkExperience.setDescription("Description");
    newWorkExperience.setNeuId("001234567");
    newWorkExperience.setCompanyName("Amazon");
    workExperiencesDao.createWorkExperience(newWorkExperience);

    List<Campus> campus = new ArrayList<>();
    campus.add(Campus.SEATTLE);
    List<StudentBasicInfo> list =
            workExperiencesDao.getStudentsWorkingInACompany(campus, 2016, "Amazon");
    assertTrue(list.size() == 1);
    assertTrue(list.get(0).getFirstName().equals("Tom"));
    assertTrue(list.get(0).getNeuId().equals("001234567"));

    list = workExperiencesDao.getStudentsWorkingInACompany(campus, 2017, "Amazon");
    assertTrue(list.size() == 0);

    list = workExperiencesDao.getStudentsWorkingInACompany(null, null, "Amazon");
    assertTrue(list.size() == 1);
  }

  /**
   * Test for getting list of coop companies that existing student has worked.
   * @throws ParseException
   */
  @Test
  public void getStudentCoopCompaniesTest() throws ParseException {
    WorkExperiences newWorkExperience = new WorkExperiences();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    newWorkExperience.setStartDate(dateFormat.parse("2016-06-01"));
    newWorkExperience.setEndDate(dateFormat.parse("2016-12-01"));
    newWorkExperience.setCurrentJob(false);
    newWorkExperience.setCoop(true);
    newWorkExperience.setTitle("Title");
    newWorkExperience.setDescription("Description");
    newWorkExperience.setNeuId("001234567");
    newWorkExperience.setCompanyName("Google");
    workExperiencesDao.createWorkExperience(newWorkExperience);

    List<Campus> list = new ArrayList<>();
    list.add(Campus.SEATTLE);
    List<StudentCoopList> temp = workExperiencesDao.getStudentCoopCompanies(list, null);
    assertTrue(temp.size() == 1);
    assertTrue(temp.get(0).getCompanies().size() == 2);

    temp = workExperiencesDao.getStudentCoopCompanies(null, 2016);
    assertTrue(temp.size() == 1);

    List<StudentCoopList> temp2 = workExperiencesDao.getStudentCoopCompanies(list, 2016);
    assertTrue(temp2.size() == 1);
    assertTrue(temp2.get(0).getCompanies().get(0).equals("Amazon"));

    temp2 = workExperiencesDao.getStudentCoopCompanies(list, 1994);
    assertTrue(temp2.isEmpty());

    workExperiencesDao.deleteWorkExperienceByNeuId("001234567");
  }

  /**
   * Test for getting the companies that students are currently working at.
   * @throws ParseException
   */
  @Test
  public void getStudentCurrentCompaniesTest() throws ParseException {
    WorkExperiences newWorkExperience = new WorkExperiences();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    newWorkExperience.setStartDate(dateFormat.parse("2017-06-01"));
    newWorkExperience.setEndDate(dateFormat.parse("2017-12-01"));
    newWorkExperience.setCurrentJob(true);
    newWorkExperience.setCoop(false);
    newWorkExperience.setTitle("Title");
    newWorkExperience.setDescription("Description");
    newWorkExperience.setNeuId("001234567");
    newWorkExperience.setCompanyName("Google");
    workExperiencesDao.createWorkExperience(newWorkExperience);

    List<Campus> list = new ArrayList<>();
    list.add(Campus.SEATTLE);
    List<StudentCoopList> temp = workExperiencesDao.getStudentCurrentCompanies(list, 2016);
    assertTrue(temp.size() == 1);
    assertTrue(temp.get(0).getCompanies().get(0).equals("Google"));

    temp = workExperiencesDao.getStudentCurrentCompanies(null, null);
    assertTrue(temp.size() == 1);

    List<StudentCoopList> temp2 = workExperiencesDao.getStudentCoopCompanies(list, 2017);
    assertTrue(temp2.isEmpty());

    workExperiencesDao.deleteWorkExperienceByNeuId("001234567");
  }

  /**
   * Test for getting top ten employers.
   * @throws ParseException
   */
  @Test
  public void getTopTenEmployersTest() throws ParseException {
    List<TopEmployer> temp = workExperiencesDao.getTopTenEmployers(null, null);
    assertTrue(temp.isEmpty());

    WorkExperiences newWorkExperience = new WorkExperiences();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    newWorkExperience.setStartDate(dateFormat.parse("2018-06-01"));
    newWorkExperience.setEndDate(dateFormat.parse("2018-12-01"));
    newWorkExperience.setCurrentJob(false);
    newWorkExperience.setCoop(false);
    newWorkExperience.setTitle("Title");
    newWorkExperience.setDescription("Description");
    newWorkExperience.setNeuId("001234567");
    newWorkExperience.setCompanyName("Amazon");
    workExperiencesDao.createWorkExperience(newWorkExperience);

    WorkExperiences newWorkExperience2 = new WorkExperiences();
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
    newWorkExperience2.setStartDate(dateFormat2.parse("2018-06-01"));
    newWorkExperience2.setEndDate(dateFormat2.parse("2018-12-01"));
    newWorkExperience2.setCurrentJob(false);
    newWorkExperience2.setCoop(true);
    newWorkExperience2.setTitle("Title");
    newWorkExperience2.setDescription("Description");
    newWorkExperience2.setNeuId("111234567");
    newWorkExperience2.setCompanyName("Aaa");
    workExperiencesDao.createWorkExperience(newWorkExperience2);

    temp = workExperiencesDao.getTopTenEmployers(null, 2016);
    assertTrue(temp.size() == 1);
    List<Campus> list = new ArrayList<>();
    list.add(Campus.SEATTLE);
    temp = workExperiencesDao.getTopTenEmployers(list, 2016);
    assertTrue(temp.size() == 1);

    list.clear();
    list.add(Campus.BOSTON);
    temp = workExperiencesDao.getTopTenEmployers(list, 1994);
    assertTrue(temp.size() == 0);
    temp = workExperiencesDao.getTopTenEmployers(list, null);
    assertTrue(temp.size() == 0);

    workExperiencesDao.deleteWorkExperienceByNeuId("111234567");
  }

  /**
   * This is test for retrieving work experience by its id
   */
  @Test
  public void getWorkExperienceIdTest() {
    int tempId = workExperiencesDao.getWorkExperiencesByNeuId("001234567").get(0).getWorkExperienceId();
    WorkExperiences workExperience1 = workExperiencesDao.getWorkExperienceById(tempId);
    assertTrue(workExperience1.getNeuId().equals("001234567"));
    assertTrue(workExperience1.getCompanyName().equals("Amazon"));
    WorkExperiences notFoundWorkExperience = workExperiencesDao.getWorkExperienceById(-10);
    assertTrue(notFoundWorkExperience == null);
  }

  /**
   * This is test for retrieving work experience by student neu id
   */
  @Test
  public void getWorkExperiencesByNeuIdTest() {
    List<WorkExperiences> listOfWorkExperiences = workExperiencesDao.getWorkExperiencesByNeuId("001234567");
    assertTrue(listOfWorkExperiences.get(0).getCompanyName().equals("Amazon"));

    assertTrue(workExperiencesDao.getWorkExperiencesByNeuId("00000000").size() == 0);
  }

  /**
   * This is test for creating, updating and deleting work experience
   * @throws ParseException
   */
  @Test
  public void createUpdateDeleteWorkExperience() throws ParseException {
    WorkExperiences newWorkExperience = new WorkExperiences();

    Students student = studentsDao.getStudentRecord("111234567");

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    newWorkExperience.setStartDate(dateFormat.parse("2017-06-01"));
    newWorkExperience.setEndDate(dateFormat.parse("2017-12-01"));
    newWorkExperience.setCurrentJob(false);
    newWorkExperience.setTitle("Title");
    newWorkExperience.setDescription("Description");
    newWorkExperience.setNeuId(student.getNeuId());
    newWorkExperience.setCompanyName("Facebook");

    // create new work experience
    workExperiencesDao.createWorkExperience(newWorkExperience);
    WorkExperiences foundWorkExperience = workExperiencesDao.getWorkExperiencesByNeuId("111234567").get(0);
    assertTrue(foundWorkExperience.getCompanyName().equals("Facebook"));

    // update found work experience
    foundWorkExperience.setDescription("Description2");
    workExperiencesDao.updateWorkExperience(foundWorkExperience);
    assertTrue(workExperiencesDao.getWorkExperiencesByNeuId("111234567").get(0).getDescription().equals("Description2"));

    // delete the work experience
    workExperiencesDao.deleteWorkExperienceById(foundWorkExperience.getWorkExperienceId());
    assertTrue(workExperiencesDao.getWorkExperienceById(foundWorkExperience.getWorkExperienceId()) == null);
  }

  /**
   * This is test for retrieving work experience with privacy control
   */
  @Test
  public void getWorkExperiencesWithPrivacyTest() {
    assertTrue(workExperiencesDao.getWorkExperiencesWithPrivacy("001234567").size()==1);
    Privacies privacy = privaciesDao.getPrivacyByNeuId("001234567");
    privacy.setCoop(false);
    privaciesDao.updatePrivacy(privacy);
    assertTrue(workExperiencesDao.getWorkExperiencesWithPrivacy("001234567").size()==0);
  }

  /**
   * Test for getting a student company ratio.
   * @throws ParseException
   */
  @Test
  public void getStudentCompanyRatioTest() throws ParseException {
    Students student = studentsDao.getStudentRecord("111234567");

    WorkExperiences newWorkExperience = new WorkExperiences();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    newWorkExperience.setStartDate(dateFormat.parse("2018-06-01"));
    newWorkExperience.setEndDate(dateFormat.parse("2018-12-01"));
    newWorkExperience.setCurrentJob(false);
    newWorkExperience.setCoop(false);
    newWorkExperience.setTitle("Title");
    newWorkExperience.setDescription("Description");
    newWorkExperience.setNeuId("001234567");
    newWorkExperience.setCompanyName("ABC");
    workExperiencesDao.createWorkExperience(newWorkExperience);

    WorkExperiences newWorkExperience2 = new WorkExperiences();
    newWorkExperience.setStartDate(dateFormat.parse("2018-06-01"));
    newWorkExperience.setEndDate(dateFormat.parse("2018-12-01"));
    newWorkExperience.setCurrentJob(false);
    newWorkExperience.setCoop(false);
    newWorkExperience.setTitle("Title");
    newWorkExperience.setDescription("Description");
    newWorkExperience.setNeuId("111234567");
    newWorkExperience.setCompanyName("ABC");
    workExperiencesDao.createWorkExperience(newWorkExperience);

    List<Campus> campus = new ArrayList<>();
    campus.add(Campus.SEATTLE);
    campus.add(Campus.BOSTON);
    List<CompanyRatio> list = workExperiencesDao.getStudentCompanyRatio(campus, "ABC");
    assertTrue(list.size()==1);
    assertTrue(list.get(0).getCount()==2);
  }
}