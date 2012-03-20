package com.github.lightning;

/**
 * Defines the comparison strategy of classes between different serializers. The
 * standard strategy of Java is SerialVersionUID but Lightning has some lighter
 * algorithm which only takes properties into account.
 * 
 * @author noctarius
 */
public enum ClassComparisonStrategy {

	/**
	 * Default Java Serialization like SerialVersionUID
	 */
	SerialVersionUID,

	/**
	 * Lightning checksum calculation
	 */
	LightningChecksum

}
