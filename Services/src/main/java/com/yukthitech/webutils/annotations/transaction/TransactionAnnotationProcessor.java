package com.yukthitech.webutils.annotations.transaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yukthitech.persistence.ITransaction;
import com.yukthitech.persistence.ITransactionManager;
import com.yukthitech.persistence.repository.RepositoryFactory;
import com.yukthitech.utils.exceptions.InvalidStateException;

/**
 * Used to wrap service methods with transaction as per given specifications.
 * @author akiran
 */
@Aspect
@Component
public class TransactionAnnotationProcessor
{
	private static Logger logger = LogManager.getLogger(TransactionAnnotationProcessor.class);
	
	/**
	 * Used to create or fetch transaction.
	 */
	@Autowired
	protected RepositoryFactory repositoryFactory;

	/**
	 * Invoked as a method wrapper for methods which are marked as transactional.
	 * @param joinPoint join point being executed
	 * @param transactional annotation used to mark as transactional
	 * @return result of method
	 */
	@Around("execution(@com.yukthitech.webutils.annotations.transaction.Transactional * *(..))  && @annotation(transactional)")
	public Object handleAttachments(ProceedingJoinPoint joinPoint, Transactional transactional) throws Throwable
	{
		TransactionType transactionType = transactional.value();
		ITransactionManager<? extends ITransaction> transactionManager = repositoryFactory.getDataStore().getTransactionManager();
		
		ITransaction transaction = null;
		
		String method = joinPoint.toShortString();
		
		//get the transaction approp
		switch (transactionType)
		{
			case NEW_OR_EXISTING:
			{
				logger.trace("For method {} using existing transaction or will create new one if one does not exist.", method);
				transaction = transactionManager.newOrExistingTransaction();
				break;
			}
			case NEW_ONLY:
			{
				logger.trace("For method {} trying to create new transaction.", method);
				transaction = transactionManager.newTransaction();
				break;
			}
			case EXISTING_ONLY:
			{
				logger.trace("For method {} trying to use existing transaction.", method);
				transaction = transactionManager.currentTransaction();
				break;
			}
			default:
				throw new InvalidStateException("Unsupported transaction type supported: {}", transactionType);
		}
		
		try
		{
			Object result = joinPoint.proceed();
			transaction.commit();
			
			logger.trace("For method {} commiting the transaction after successful invocation.", method);
			return result;
		} catch(Exception ex)
		{
			logger.trace("For method {} rolling back the transaction because of error: {}", method, "" + ex);
			
			if(!transaction.isClosed())
			{
				transaction.rollback();
			}
			else
			{
				logger.trace("For method {} found transaction to be pre closed. Hence skipping rollback", method);
			}
			
			throw ex;
		} finally
		{
			if(!transaction.isClosed())
			{
				transaction.close();
			}
			else
			{
				logger.trace("For method {} found transaction to be pre closed", method);				
			}
		}
	}
}
