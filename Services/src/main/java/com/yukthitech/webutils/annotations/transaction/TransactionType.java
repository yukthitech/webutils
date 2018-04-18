package com.yukthitech.webutils.annotations.transaction;

/**
 * Enumeration of transaction types.
 * @author akiran
 */
public enum TransactionType
{
	/**
	 * Creates a new transaction. If this already opened a transaction,
	 * this method will throw an exception.
	 */
	NEW_ONLY,
	
	/**
	 * Uses an existing transaction, if transaction is not already active, a new
	 * transaction will be opened.
	 */
	NEW_OR_EXISTING,
	
	/**
	 * Uses existing transaction. If no active transaction exists by current thread,
	 * exception will be thrown.
	 */
	EXISTING_ONLY;
}
