package com.minecolonies.api.util;


/**
 * Logging utility class.
 */
public final class Log
{
	/**
	 * Mod logger.
	 */
	private static final Logger logger = LogManager.getLogger(Constants.MOD_ID);

	/**
	 * Private constructor to hide the public one.
	 */
	private Log()
	{
		/*
		 * Intentionally left empty.
		 */
	}

	/**
	 * Getter for the minecolonies Logger.
	 *
	 * @return the logger.
	 */
	public static Logger getLogger()
	{
		return logger;
	}
}