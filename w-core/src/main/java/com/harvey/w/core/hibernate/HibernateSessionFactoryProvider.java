package com.harvey.w.core.hibernate;

import java.lang.reflect.Field;

import org.hibernate.SessionFactory;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

public class HibernateSessionFactoryProvider implements SessionFactoryProvider {

	public static final HibernateSessionFactoryProvider INSTANCE = new HibernateSessionFactoryProvider();

	// private FastMethod transactionInfoHolderMethodAccess;
	private FastMethod transactionInfoMethodAccess;
	private ThreadLocal<?> transactionInfoStore;

	public HibernateSessionFactoryProvider() {
		try {
			Field field = TransactionAspectSupport.class.getDeclaredField("transactionInfoHolder");
			field.setAccessible(true);
			transactionInfoStore = (ThreadLocal<?>) field.get(null);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public SessionFactory getSessionFactory() {
		try {
			PlatformTransactionManager tm = this.getPlatformTransactionManager();
			if (tm instanceof org.springframework.orm.hibernate4.HibernateTransactionManager) {
				return ((org.springframework.orm.hibernate4.HibernateTransactionManager) tm).getSessionFactory();
			} else if (tm instanceof org.springframework.orm.hibernate5.HibernateTransactionManager) {
				return ((org.springframework.orm.hibernate5.HibernateTransactionManager) tm).getSessionFactory();
			}
			throw new IllegalArgumentException("UnSupported TransactionManager:" + tm);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public PlatformTransactionManager getPlatformTransactionManager() throws Exception {
		Object txInfo = transactionInfoStore.get();
		if (txInfo != null) {
			if (transactionInfoMethodAccess == null) {
				FastClass fc = FastClass.create(txInfo.getClass());
				transactionInfoMethodAccess = fc.getMethod("getTransactionManager", Constants.EMPTY_CLASS_ARRAY);
			}
			return (PlatformTransactionManager) transactionInfoMethodAccess.invoke(txInfo, new Object[0]);
		}
		throw new NoTransactionException("No transaction aspect-managed TransactionStatus in scope");
	}
}
