package com.github.lightning;

public enum SerializationStrategy {

	/**
	 * This strategy does not force same instances to become same instances on
	 * deserialization since only values are written to the stream.<br>
	 * To be clear, deserialized instances of same objects are non
	 * identity-equal!
	 */
	SpeedOptimized,

	/**
	 * This strategy forces same instances to become same instances on
	 * deserialization. This needs to collect instances by hashCode on both
	 * sides while serialization and deserialization, which in case needs time.<br>
	 * To be clear, deserialized instances of same objects are identity-equal!
	 */
	SizeOptimized

}
