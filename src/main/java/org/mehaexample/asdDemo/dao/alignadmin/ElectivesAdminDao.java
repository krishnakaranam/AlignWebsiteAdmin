package org.mehaexample.asdDemo.dao.alignadmin;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.mehaexample.asdDemo.dao.alignprivate.StudentsDao;
import org.mehaexample.asdDemo.model.alignadmin.ElectivesAdmin;

public class ElectivesAdminDao {
  private SessionFactory factory;

  private StudentsDao studentDao;

  /**
   * Default Constructor.
   */
  public ElectivesAdminDao() {
    studentDao = new StudentsDao();
    // it will check the hibernate.cfg.xml file and load it
    // next it goes to all table files in the hibernate file and loads them
    this.factory = AdminSessionFactory.getFactory();
  }

  /**
   * Test constructor.
   *
   * @param test set true to construct test dao.
   */
  public ElectivesAdminDao(boolean test) {
    studentDao = new StudentsDao(true);
    if (test) {
      this.factory = AdminTestSessionFactory.getFactory();
    }
  }

  /**
   * Get the list of Electives of a student with corresponding neu Id.
   * This method will also get the GPAs, retake, and plagiarism field
   * from the database.
   *
   * @param neuId student Neu Id.
   * @return list of electives taken by the student.
   */
  public List<ElectivesAdmin> getElectivesByNeuId(String neuId) {
    Session session = factory.openSession();
    try {
      org.hibernate.query.Query query = session.createQuery("from ElectivesAdmin where neuId = :neuId");
      query.setParameter("neuId", neuId);
      return (List<ElectivesAdmin>) query.list();
    } finally {
      session.close();
    }
  }

  /**
   * Get an elective based on the elective Id.
   *
   * @param electiveId to get the corresponding elective.
   * @return Elective if found, null otherwise.
   */
  public ElectivesAdmin getElectiveById(int electiveId) {
    Session session = factory.openSession();
    try {
      org.hibernate.query.Query query = session.createQuery("from ElectivesAdmin where electiveId = :electiveId");
      query.setParameter("electiveId", electiveId);
      List<ElectivesAdmin> list = query.list();
      if (list.isEmpty()) {
        return null;
      }
      return list.get(0);
    } finally {
      session.close();
    }
  }

  /**
   * This is the function to add an Elective for a given student into database.
   *
   * @param elective elective to be added; not null.
   * @return true if insert successfully. Otherwise throws exception.
   */
  public synchronized ElectivesAdmin addElective(ElectivesAdmin elective) {
    if (elective == null) {
      throw new IllegalArgumentException("Elective argument cannot be null.");
    }

    Transaction tx = null;
    Session session = factory.openSession();

    if (studentDao.ifNuidExists(elective.getNeuId())) {
      try {
        tx = session.beginTransaction();
        session.save(elective);
        tx.commit();
      } catch (HibernateException e) {
        if (tx != null) tx.rollback();
        throw new HibernateException(e);
      } finally {
        session.close();
      }
    } else {
      throw new HibernateException("The student with a given nuid doesn't exists");
    }
    return elective;
  }

  /**
   * Update an elective of a student.
   *
   * @param elective updated Elective.
   * @return true if updated, false otherwise.
   */
  public synchronized boolean updateElectives(ElectivesAdmin elective) {
    if (getElectiveById(elective.getElectiveId()) == null) {
      throw new HibernateException("Elective Id cannot be null.");
    }
    Transaction tx = null;
    Session session = factory.openSession();
    try {
      tx = session.beginTransaction();
      session.saveOrUpdate(elective);
      tx.commit();
      return true;
    } catch (HibernateException e) {
      if (tx != null) tx.rollback();
      throw new HibernateException(e);
    } finally {
      session.close();
    }
  }

  /**
   * Delete an elective based on the Elective Id.
   *
   * @param id elective Id.
   * @return true if deleted, false otherwise.
   */
  public synchronized boolean deleteElectiveRecord(int id) {
    ElectivesAdmin electives = getElectiveById(id);
    if (electives == null) {
      throw new HibernateException("Elective Id cannot be found.");
    }
    Transaction tx = null;
    Session session = factory.openSession();
    try {
      tx = session.beginTransaction();
      session.delete(electives);
      tx.commit();
      return true;
    } catch (HibernateException e) {
      if (tx != null) tx.rollback();
      throw new HibernateException(e);
    } finally {
      session.close();
    }
  }
}