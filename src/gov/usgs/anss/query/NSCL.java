package gov.usgs.anss.query;

import java.util.Comparator;

/**
 * An object for the encapsulation of data and methods for data channels named
 * according to SEED conventions.
 * 
 * All NSCL fields are fixed width, ASCII, alphanumeric fields left justified
 * (no leading spaces), and paded with spaces (after the field’s contents).
 * Network Operator Code, 2 ULN (upper case, lower case or numeric digits).
 * Station Identifier, 5 UN.
 * Channel Identifier, 2 UN.
 * Location Identifier, 2 UN.
 *
 * @author richardg
 */
public class NSCL {

	private String network, station, channel, location;

	/**
	 * TODO: handle whitespace and/or wildcards...?
	 * @param network
	 * @param station
	 * @param channel
	 * @param location
	 */
	public NSCL(String network, String station, String channel, String location) {
		setNetwork(network);
		setStation(station);
		setChannel(channel);
		setLocation(location);
	}

	/**
	 * Returns a new NSCL object constructed from the NNSSSSSCCCLL input String.
	 * @param input 12 character String formatted as NNSSSSSCCLL.
	 * @return a new NSCL representation of the input NSCL.
	 */
	public static NSCL stringToNSCL(String input) {
		if (input.length() != 12) {
			throw new IllegalArgumentException("NSCL code must be 12 characters long.");
		}
		return new NSCL(input.substring(0, 2), input.substring(2, 7),
				input.substring(7, 10), input.substring(10, 12));
	}

	/**
	 * @return the network
	 */
	public String getNetwork() {
		return (network + "  ").substring(0, 2);
	}

	/**
	 * SEED: 2 ULN
	 * @param network the network to set
	 */
	public void setNetwork(String network) {
		if (network.length() > 2) {
			throw new IllegalArgumentException("Network code must be less than or equal to 2 characters in length.");
		}
		this.network = network;
	}

	/**
	 * @return the station
	 */
	public String getStation() {
		return (station + "     ").substring(0, 5);
	}

	/**
	 * SEED: 5 UN
	 * @param station the station to set
	 */
	public void setStation(String station) {
		if (station.length() > 5) {
			throw new IllegalArgumentException("Station code must be less than or equal to 5 characters in length.");
		}
		this.station = station;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return (channel + "   ").substring(0, 3);
	}

	/**
	 * SEED: 3 UN
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		if (channel.length() != 3) {
			throw new IllegalArgumentException("Channel code must be less than or equal to 3 characters in length.");
		}
		this.channel = channel;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return (location + "  ").substring(0, 2);
	}

	/**
	 * SEED: 2 UN
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		if (location.length() != 2) {
			throw new IllegalArgumentException("Location code must be less than or equal to 2 characters in length.");
		}
		this.location = location;
	}

	/**
	 * True if both NSCL objects are not null and both NSCL String
	 * components are equal.
	 * Throws an NullPointerException if any of the NSCL components in this are null.
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		NSCL other = (NSCL) obj;
		if (other == null) {
			return false;
		}

		return this.network.equals(other.network) &&
				this.station.equals(other.station) &&
				this.channel.equals(other.channel) &&
				this.location.equals(other.location);
	}

	@Override
	public String toString() {
		return (getNetwork() + getStation() + getChannel() + getLocation());
	}

	public static Comparator NetworkComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			NSCL s1 = (NSCL) o1;
			NSCL s2 = (NSCL) o2;
			return s1.getNetwork().compareTo(s2.getNetwork());
		}
	};

	public static Comparator StationComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			int result = NetworkComparator.compare(o1, o2);
			if (result != 0) {
				return result;
			}
			NSCL s1 = (NSCL) o1;
			NSCL s2 = (NSCL) o2;
			return s1.getStation().compareTo(s2.getStation());
		}
	};

	public static Comparator ChannelComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			int result = StationComparator.compare(o1, o2);
			if (result != 0) {
				return result;
			}
			NSCL s1 = (NSCL) o1;
			NSCL s2 = (NSCL) o2;
			return s1.getChannel().compareTo(s2.getChannel());
		}
	};

	public static Comparator LocationComparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			int result = ChannelComparator.compare(o1, o2);
			if (result != 0) {
				return result;
			}
			NSCL s1 = (NSCL) o1;
			NSCL s2 = (NSCL) o2;
			return s1.getLocation().compareTo(s2.getLocation());
		}
	};
}
