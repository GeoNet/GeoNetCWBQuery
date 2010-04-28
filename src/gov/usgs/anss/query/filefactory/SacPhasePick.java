/*
 * Copyright 2010, Institute of Geological & Nuclear Sciences Ltd or
 * third-party contributors as indicated by the @author tags.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package gov.usgs.anss.query.filefactory;

import nz.org.geonet.HashCodeUtil;

/**
 *
 * @author geoffc
 */
public class SacPhasePick implements Comparable<SacPhasePick> {

    protected String phaseName;

    /**
     * Get the value of phaseName
     *
     * @return the value of phaseName
     */
    public String getPhaseName() {
        return phaseName;
    }

    /**
     * Set the value of phaseName
     *
     * @param phaseName new value of phaseName
     */
    public void setPhaseName(String phaseName) {
        this.phaseName = phaseName;
    }
    protected double timeAfterOriginInSeconds;

    /**
     * Get the value of timeAfterOriginInSeconds
     *
     * @return the value of timeAfterOriginInSeconds
     */
    public double getTimeAfterOriginInSeconds() {
        return timeAfterOriginInSeconds;
    }

    /**
     * Set the value of timeAfterOriginInSeconds
     *
     * @param timeAfterOriginInSeconds new value of timeAfterOriginInSeconds
     */
    public void setTimeAfterOriginInSeconds(double timeAfterOriginInSeconds) {
        this.timeAfterOriginInSeconds = timeAfterOriginInSeconds;
    }

    public SacPhasePick() {
        
    }

    public SacPhasePick(String phaseName, double timeAfterOriginInSeconds) {
        this.phaseName = phaseName;
        this.timeAfterOriginInSeconds = timeAfterOriginInSeconds;
    }

    public int compareTo(SacPhasePick o) {
        return (int) ((this.timeAfterOriginInSeconds * 10000.0d) - (o.getTimeAfterOriginInSeconds() * 10000.0d));
    }

    @Override
    public boolean equals(
            Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj.getClass() == this.getClass()) {
            final SacPhasePick other = (SacPhasePick) obj;

            // Only compare picks to millisecond for equals.
            if (getPhaseName().equals(other.getPhaseName()) && 
                    (int) (getTimeAfterOriginInSeconds() * 1000) == (int) (other.getTimeAfterOriginInSeconds() * 1000)
                    ) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash(result, getPhaseName());
        result = HashCodeUtil.hash(result, getTimeAfterOriginInSeconds());
        return result;
    }
}
