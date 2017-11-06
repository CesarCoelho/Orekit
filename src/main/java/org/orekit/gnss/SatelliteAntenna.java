/* Copyright 2002-2017 CS Systèmes d'Information
 * Licensed to CS Systèmes d'Information (CS) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * CS licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.orekit.gnss;

import java.util.Map;

import org.orekit.time.AbsoluteDate;

/**
 * GNSS satellite antenna model.
 *
 * @author Luc Maisonobe
 * @since 9.1
 * @see <a href="ftp://www.igs.org/pub/station/general/antex14.txt">ANTEX: The Antenna Exchange Format, Version 1.4</a>
 *
 */
public class SatelliteAntenna extends Antenna {

    /** Satellite system. */
    private final SatelliteSystem satelliteSystem;

    /** PRN number. */
    private final int prnNumber;

    /** Satellite code. */
    private final int satelliteCode;

    /** COSPAR ID. */
    private final String cosparID;

    /** Start of validity. */
    private final AbsoluteDate validFrom;

    /** End of validity. */
    private final AbsoluteDate validUntil;

    /** Simple constructor.
     * @param type antenna type
     * @param sinexCode sinex code
     * @param patterns frequencies patterns
     * @param satelliteSystem satellite system
     * @param prnNumber PRN number
     * @param satelliteCode satellite code
     * @param cosparID COSPAR ID
     * @param validFrom start of validity
     * @param validUntil end of validity
     */
    public SatelliteAntenna(final String type, final String sinexCode,
                            final Map<Frequency, FrequencyPattern> patterns,
                            final SatelliteSystem satelliteSystem, final int prnNumber,
                            final int satelliteCode, final String cosparID,
                            final AbsoluteDate validFrom, final AbsoluteDate validUntil) {
        super(type, sinexCode, patterns);
        this.satelliteSystem = satelliteSystem;
        this.prnNumber       = prnNumber;
        this.satelliteCode   = satelliteCode;
        this.cosparID        = cosparID;
        this.validFrom       = validFrom;
        this.validUntil      = validUntil;
    }

    /** Get satellite system.
     * @return satellite system
     */
    public SatelliteSystem getSatelliteSystem() {
        return satelliteSystem;
    }

    /** Get PRN number.
     * @return PRN number
     */
    public int getPrnNumber() {
        return prnNumber;
    }

    /** Get satellite code.
     * @return satellite code
     */
    public int getSatelliteCode() {
        return satelliteCode;
    }

    /** Get COSPAR ID.
     * @return COSPAR ID
     */
    public String getCosparID() {
        return cosparID;
    }

    /** Get start of validity.
     * @return start of validity
     */
    public AbsoluteDate getValidFrom() {
        return validFrom;
    }

    /** Get end of validity.
     * @return end of validity
     */
    public AbsoluteDate getValidFUntil() {
        return validUntil;
    }

}
