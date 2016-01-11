import com.yukthi.webutils.common.annotations.NonDisplayable;
	/**
	 * Indicates if this is secured file or not
	 */
	@NonDisplayable
	private boolean secured = true;
	
	
	

	/**
	 * Checks if is indicates if this is secured file or not.
	 *
	 * @return the indicates if this is secured file or not
	 */
	public boolean isSecured()
	{
		return secured;
	}

	/**
	 * Sets the indicates if this is secured file or not.
	 *
	 * @param secured the new indicates if this is secured file or not
	 */
	public void setSecured(boolean secured)
	{
		this.secured = secured;
	}