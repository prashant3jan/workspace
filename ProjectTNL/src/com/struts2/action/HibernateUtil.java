package com.struts2.action;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	//private static final SessionFactory sessionFactory = buildSessionFactory();
	
	public static Session getHibernateSession() {

	    final SessionFactory sf = new Configuration()
	        .configure("hibernate.cfg.xml").buildSessionFactory();

	    // factory = new Configuration().configure().buildSessionFactory();
	    final Session session = sf.openSession();
	    return session;
	    }

}
