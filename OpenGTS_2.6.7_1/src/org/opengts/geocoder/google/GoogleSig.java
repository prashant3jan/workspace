// ----------------------------------------------------------------------------
// Copyright 2007-2020, GeoTelematic Solutions, Inc.
// All rights reserved
// ----------------------------------------------------------------------------
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
// http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// ----------------------------------------------------------------------------
// Change History:
//  2011/07/01  Martin D. Flynn
//     -Initial release
//  2015/09/24  Martin D. Flynn
//     -Made call to "mac.doFinal(..)" thread safe
// ----------------------------------------------------------------------------
package org.opengts.geocoder.google;

import java.math.BigInteger;
import java.net.URL;
import java.net.MalformedURLException;

import org.opengts.util.MACProvider;
import org.opengts.util.RTConfig;
import org.opengts.util.Base64;
import org.opengts.util.Print;

public class GoogleSig
{

    // ------------------------------------------------------------------------

    private static final String ARG_SIGNATURE_  = "&signature=";
    
    private static final String HMACSHA1        = "HmacSHA1";

    // ------------------------------------------------------------------------

    private static MACProvider macProvider = null;

    public static void SetMACProvider(MACProvider mp)
    {
        GoogleSig.macProvider = mp;
    }

    // ------------------------------------------------------------------------

    /**
    *** Returns true if the specified String is null, blank, or contains only spaces
    **/
    public static boolean IsBlank(String s)
    {
        if (s == null) {
            return true;
        } else
        if (s.trim().equals("")) {
            return true;
        } else {
            return false;
        }
    }

    /**
    *** Returns true if the specified byte array is null, or has zero length
    **/
    public static boolean IsEmpty(byte b[])
    {
        if (b == null) {
            return true;
        } else
        if (b.length <= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
    *** Returns true if the String contains any of the specified characters
    **/
    public static boolean ContainsChar(String s, char... ca)
    {
        if (s == null) {
            return false;
        } else
        if ((ca == null) || (ca.length <= 0)) {
            return false;
        } else {
            char sca[] = s.toCharArray();
            for (char sc : s.toCharArray()) {
                for (char cc : ca) {
                    if (sc == cc) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    private Object keyMac = null;

    /**
    *** Constructor
    **/
    public GoogleSig(String keyStr)
    {
        super();
    }

    // ------------------------------------------------------------------------

    /**
    *** Thread safe call to (javax.crypto.Mac).doFinal(...)
    **/
    private static byte[] _MacDoFinal(javax.crypto.Mac mac, byte b[])
    {
        byte pqb[] = null;
        if ((mac != null) && !GoogleSig.IsEmpty(b)) {
            synchronized (mac) {
                try {
                    pqb = mac.doFinal(b); // _MacDoFinal (now thread safe)
                } catch (Throwable th) {
                    pqb = null;
                }
            }
        }
        return pqb;
    }

    // ------------------------------------------------------------------------

    /**
    *** Calculate and append Google "&signature=" hash.
    *** @param urlStr  The URL string to which the hash is appended.
    *** @return The URL string with the "&signature=" hash appended, or null if unable to sign
    **/
    public String signURL(String urlStr)
    {
        // HMAC-SHA1 signature code here
        return null;
    }

    // ------------------------------------------------------------------------

    /**
    *** Checks that the URL contains a "&signature=" parameter, and that the
    *** signature is valid.
    **/
    public boolean validateURL(String urlStr)
    {
        // -- HMAC-SHA1 signature validation code here
        return false;
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    public static void main(String argv[])
    {
        RTConfig.setCommandLineArgs(argv);

        if (RTConfig.hasProperty("url")) {
            String urlStr = RTConfig.getString("url",null);
            String keyStr = RTConfig.getString("key",null);
            if (GoogleSig.IsBlank(urlStr) || GoogleSig.IsBlank(keyStr)) {
                Print.sysPrintln("ERROR: Missing url or key");
                System.exit(99);
            }
            GoogleSig gs = new GoogleSig(keyStr);
            String sigURL = urlStr.contains(ARG_SIGNATURE_)? urlStr : gs.signURL(urlStr);
            Print.sysPrintln("");
            Print.sysPrintln("OriginalURL : " + urlStr);
            Print.sysPrintln("SignatureURL: " + sigURL);
            Print.sysPrintln("Validated   : " + gs.validateURL(sigURL));
            Print.sysPrintln("");
            System.exit(0);
        }

    }
    
}
