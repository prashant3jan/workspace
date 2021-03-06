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
// Description:
//  Partial implementation of a ClientPacketHandler
// ----------------------------------------------------------------------------
// Change History:
//  2014/10/22  Martin D. Flynn
//     -Initial release
// ----------------------------------------------------------------------------
package org.opengts.util;

import java.lang.*;
import java.util.*;

/**
*** Container for an X/Y pair.<br>
*** Used by CurveFit as a point container.
**/
public class XYPair
{

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    private static final String LIST_START[]        = { "[", "(" };
    private static final String LIST_END[]          = { "]", ")" };
    private static final char   LIST_SEPARATOR_CHAR = '|';

    /**
    *** Gets the index of the start-of-list character
    **/
    public static int IndexOfListStartChar(String xyListStr, int fromNdx)
    {
        return StringTools.indexOf(xyListStr, fromNdx, LIST_START);
    }

    /**
    *** Gets the index of the end-of-list character
    **/
    public static int IndexOfListEndChar(String xyListStr, int fromNdx)
    {
        return StringTools.indexOf(xyListStr, fromNdx, LIST_END);
    }

    /**
    *** Returns true if the specified list starts with a XYPair list character
    **/
    public static boolean startsWithListChar(String xyListStr)
    {
        if (xyListStr == null) {
            return false;
        } else {
            for (String sol : LIST_START) {
                if (xyListStr.startsWith(sol)) {
                    return true;
                }
            }
            return false;
        }
    }

    // ------------------------------------------------------------------------

    /**
    *** Parse XY pair list.
    *** Leading/Trailing list characters (..) or [..] MUST NOT be specified.
    *** Does not return null.
    **/
    private static XYPair[] _ParseXYPair(String xyListStr)
    {
        // "0.00,0.00|0.50,0.50|1.00,1.00"
        char listSep = LIST_SEPARATOR_CHAR;

        /* profile string not specified */
        if (StringTools.isBlank(xyListStr)) {
            // -- no list specified
            return new XYPair[0];
        }

        /* parse */
        String P[] = StringTools.split(xyListStr,listSep);
        Vector<XYPair> apiList = new Vector<XYPair>();
        for (int i = 0; i < P.length; i++) {
            XYPair api = new XYPair(P[i]);
            if (api.isValid()) {
                apiList.add(api);
            } else {
                Print.logWarn("Found invalid XYPair: " + P[i]);
            }
        }

        /* return array */
        if (apiList.size() >= 1) {
            return apiList.toArray(new XYPair[apiList.size()]);
        } else {
            // -- list is empty
            return new XYPair[0];
        }

    }

    /**
    *** Parse XY pair list.
    *** Leading/Trailing list characters (..) or [..], are optional.
    *** Does not return null.
    **/
    public static XYPair[] ParseXYPair(String xyListStr)
    {
        // "0.00,0.00|0.50,0.50|1.00,1.00"

        /* remove optional leading trailing list characters */
        int sNdx = IndexOfListStartChar(xyListStr,0/*fromNdx*/);
        int eNdx = (sNdx >= 0)? IndexOfListEndChar(xyListStr,sNdx+1) : -1;
        if ((sNdx >= 0) || (eNdx >= 0)) {
            // -- trim to list only
            int S = (sNdx >= 0)? (sNdx + 1) : 0;
            int E = (eNdx >= 0)? eNdx : xyListStr.length();
            xyListStr = xyListStr.substring(S,E).trim(); // just the list
        }

        /* parse/return */
        return _ParseXYPair(xyListStr);

    }

    /**
    *** Parse XY pair list.
    *** Leading/Trailing list characters (..) or [..], are required.
    *** Does not return null.
    **/
    public static XYPair[] ParseXYPair(String xyListStr, int fromNdx)
    {
        // "xyz:(0.00,0.00|0.50,0.50|1.00,1.00)"

        /* find/remove leading/trailing brackets */
        // -- leading/trailing list characters required
        if (fromNdx < 0) { fromNdx = 0; }
        int sNdx = IndexOfListStartChar(xyListStr,fromNdx);
        int eNdx = (sNdx >= 0)? IndexOfListEndChar(xyListStr,sNdx+1) : -1;
        if ((sNdx >= 0) && (eNdx > (sNdx + 1))) {
            // -- trim to list only
            xyListStr = xyListStr.substring(sNdx+1,eNdx).trim(); // just the list
        } else {
            // -- invalid list, no list specified
            xyListStr = null;
        }

        /* parse */
        return _ParseXYPair(xyListStr);

    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
    *** Returns a copy of the specified XYPair instance, with X,Y scaling
    **/
    public static XYPair Copy(XYPair xy, double scaleX, double scaleY)
    {
        return (xy != null)? new XYPair(xy,scaleX,scaleY) : null;
    }

    /**
    *** Returns a copy of the specified XYPair instance
    **/
    public static XYPair Copy(XYPair xy)
    {
        return XYPair.Copy(xy, Double.NaN, Double.NaN);
    }

    // --------------------------------

    /**
    *** Returns a deep copy of the specified XYPair list
    **/
    public static XYPair[] CreateList(XYPair xyPair[])
    {
        return XYPair.CreateList(xyPair, Double.NaN, Double.NaN);
    }

    /**
    *** Returns a copy of the specified XYPair list, with X,Y scaling
    **/
    public static XYPair[] CreateList(XYPair xyPair[], double scaleX, double scaleY)
    {
        if (xyPair != null) {
            XYPair newXYP[] = new XYPair[xyPair.length];
            for (int i = 0; i < xyPair.length; i++) {
                newXYP[i] = XYPair.Copy(xyPair[i], scaleX, scaleY);
            }
            return newXYP;
        } else {
            return null;
        }
    }

    // --------------------------------

    /**
    *** Returns a deep copy of the specified XYPair list
    **/
    public static XYPair[] CreateList(double xyList[][])
    {
        return XYPair.CreateList(xyList, Double.NaN, Double.NaN);
    }

    /**
    *** Returns a copy of the specified XYPair list
    **/
    public static XYPair[] CreateList(double xyList[][], double scaleX, double scaleY)
    {
        if (xyList != null) {
            XYPair newXYP[] = new XYPair[xyList.length];
            for (int i = 0; i < xyList.length; i++) {
                if ((xyList[i] != null) && (xyList[i].length >= 2)) {
                    double x = XYPair.ScaleX(xyList[i][0], scaleX);
                    double y = XYPair.ScaleY(xyList[i][1], scaleY);
                    newXYP[i] = new XYPair(x, y);
                } else {
                    newXYP[i] = null;
                }
            }
            return newXYP;
        } else {
            return null;
        }
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
    *** Returns true if the list is sorted by X in ascending order, with no duplicates
    *** @param xyPair  An array of XYPair's
    *** @param dupOK   True to allow duplicate X entries
    **/
    public static boolean IsSortedByX(XYPair xyPair[], boolean dupOK)
    {
        if (xyPair != null) {
            double lastX = Double.NaN;
            for (int i = 0; i < xyPair.length; i++) {
                double X = xyPair[i].getX();
                if (i > 0) {
                    if (lastX > X) {
                        return false;
                    } else
                    if (!dupOK && (lastX == X)) {
                        return false;
                    }
                }
                lastX = X;
            }
            return true;
        } else {
            return false;
        }
    }
    
    /**
    *** Sorts the XYPair list by the X value
    **/
    public static XYPair[] SortByX(XYPair xyPair[])
    {
        return ListTools.sort(xyPair, new Comparator<XYPair>() {
            public int compare(XYPair xy1, XYPair xy2) {
                if (xy1 == xy2 ) { return  0; } // same object
                if (xy1 == null) { return  1; } // sort nulls to end of list
                if (xy2 == null) { return -1; } // sort nulls to end of list
                double X1 = xy1.getX(), X2 = xy2.getX();
                return (X1 == X2)? 0 : (X1 < X2)? -1 : 1;
            }
        });
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
    *** Minimum value for X in the specified list
    **/
    public static double GetMinimumX(XYPair xyPair[])
    {
        if (xyPair != null) {
            double min = Double.NaN;
            for (int i = 0; i < xyPair.length; i++) {
                double X = xyPair[i].getX();
                if ((i == 0) || (X < min)) {
                    min = X;
                }
            }
            return min;
        } else {
            return Double.NaN;
        }
    }

    /**
    *** Minimum value for X in the specified list
    **/
    public static double GetMaximumX(XYPair xyPair[])
    {
        if (xyPair != null) {
            double max = Double.NaN;
            for (int i = 0; i < xyPair.length; i++) {
                double X = xyPair[i].getX();
                if ((i == 0) || (X > max)) {
                    max = X;
                }
            }
            return max;
        } else {
            return Double.NaN;
        }
    }

    /**
    *** Range of X in the specified list
    **/
    public static double GetRangeX(XYPair xyPair[])
    {
        if (xyPair != null) {
            double min = Double.NaN;
            double max = Double.NaN;
            for (int i = 0; i < xyPair.length; i++) {
                double X = xyPair[i].getX();
                if ((i == 0) || (X < min)) {
                    min = X;
                }
                if ((i == 0) || (X > max)) {
                    max = X;
                }
            }
            return max - min;
        } else {
            return Double.NaN;
        }
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    protected double  X = 0.0; // X
    protected double  Y = 0.0; // Y
    protected boolean isValid  = false;

    /**
    *** Copy Constructor, with X,Y scaling
    **/
    public XYPair(XYPair xyPair, double scaleX, double scaleY) 
    {
        if (xyPair != null) {
            // -- scale X
            double x = xyPair.X;
            if (!Double.isNaN(x) && !Double.isNaN(scaleX) && (scaleX != 0.0)) {
                x *= scaleX;
            }
            // -- scale Y
            double y = xyPair.Y;
            if (!Double.isNaN(y) && !Double.isNaN(scaleY) && (scaleY != 0.0)) {
                y *= scaleY;
            }
            // -- save
            this.X       = x;
            this.Y       = y;
            this.isValid = xyPair.isValid;
        } else {
            this.isValid = false;
        }
    }

    /**
    *** Copy Constructor
    **/
    public XYPair(XYPair xyPair) 
    {
        this(xyPair, Double.NaN, Double.NaN);
    }

    /**
    *** X/Y Constructor
    **/
    public XYPair(double x, double y) 
    {
        this.X       = x;
        this.Y       = y;
        this.isValid = !Double.isNaN(this.X) && !Double.isNaN(this.Y);
    }

    /**
    *** String Constructor
    **/
    public XYPair(String xyStr) 
    {
        // "0.25,0.25" or "25,25"
        if (!StringTools.isBlank(xyStr)) {
            String L[] = StringTools.split(xyStr,',');
            if (ListTools.size(L) >= 2) {
                // -- input/output level
                this.X = StringTools.parseDouble(L[0], Double.NaN);
                this.Y = StringTools.parseDouble(L[1], Double.NaN);
                // -- invalid level?
                if (Double.isNaN(this.X) || Double.isNaN(this.Y)) {
                    Print.logError("Invalid XYPair value: " + xyStr);
                    this.X       = 0.0;
                    this.Y       = 0.0;
                    this.isValid = false;
                } else {
                    this.isValid = true;
                }
            } else {
                Print.logError("Invalid XYPair value: " + xyStr);
                this.isValid = false;
            }
        } else {
            Print.logError("Null XYPair value");
            this.isValid = false;
        }
    }

    // ------------------------------------------------------------------------

    /** 
    *** Returns true if valid.
    **/
    public boolean isValid() 
    {
        return this.isValid;
    }

    // ------------------------------------------------------------------------

    /**
    *** Gets the value for X
    **/
    public double getX()
    {
        return this.X;
    }

    /**
    *** Gets the value for X
    **/
    public double X()
    {
        return this.X;
    }

    /**
    *** Multiplies X by the specified value, and saves the new value of X
    *** Ignored if X is NaN, or if 'scaleX' is NaN or zero.
    *** @return The new valie of X
    **/
    public double scaleX(double scaleX)
    {
        this.X = XYPair.ScaleX(this.X, scaleX);
        return this.X;
    }

    /**
    *** Multiplies X by the specified value, and returns the new value of X
    *** Returns X, if X is NaN, or if 'scaleX' is NaN or zero.
    *** @return The new value of X
    **/
    public static double ScaleX(double X, double scaleX)
    {
        if (!Double.isNaN(X) && !Double.isNaN(scaleX) && (scaleX != 0.0)) {
            X *= scaleX;
        }
        return X;
    }

    // ------------------------------------------------------------------------

    /**
    *** Gets the value for Y
    **/
    public double getY()
    {
        return this.Y;
    }

    /**
    *** Gets the value for Y
    **/
    public double Y()
    {
        return this.Y;
    }

    /**
    *** Multiplies Y by the specified value, and saves the new value of Y
    *** Ignored if Y is NaN, or if 'scaleY' is NaN or zero.
    *** @return The new valie of Y
    **/
    public double scaleY(double scaleY)
    {
        this.Y = XYPair.ScaleY(this.Y, scaleY);
        return this.Y;
    }

    /**
    *** Multiplies Y by the specified value, and returns the new value of Y
    *** Returns Y, if Y is NaN, or if 'scaleY' is NaN or zero.
    *** @return The new value of Y
    **/
    public static double ScaleY(double Y, double scaleY)
    {
        if (!Double.isNaN(Y) && !Double.isNaN(scaleY) && (scaleY != 0.0)) {
            Y *= scaleY;
        }
        return Y;
    }

    // ------------------------------------------------------------------------

    /**
    *** Returns a String representation of this instance
    **/
    public String toString()
    {
        return this.toString(null,null,null).toString();
    }

    /**
    *** Returns a String representation of this instance
    **/
    public String toString(String xFmt, String yFmt)
    {
        return this.toString(null,xFmt,yFmt).toString();
    }

    /**
    *** Appends a String representation of this instance to the specified StringBuffer
    **/
    public StringBuffer toString(StringBuffer sb)
    {
        return this.toString(sb,null,null);
    }

    /**
    *** Appends a String representation of this instance to the specified StringBuffer
    **/
    public StringBuffer toString(StringBuffer sb, String xFmt, String yFmt)
    {
        if (sb == null) { sb = new StringBuffer(); }
        if (!StringTools.isBlank(xFmt)) {
            sb.append(StringTools.format(this.getX(),xFmt));
        } else {
            sb.append(this.getX());
        }
        sb.append(",");
        if (!StringTools.isBlank(yFmt)) {
            sb.append(StringTools.format(this.getY(),yFmt));
        } else {
            sb.append(this.getY());
        }
        return sb;
    }

    // ------------------------------------------------------------------------

    /** 
    *** Returns a properly formatted XYPair list String
    **/
    public static String ToListString(XYPair xyList[])
    {
        return XYPair.ToListString(xyList,true,null,null,null).toString();
    }

    /** 
    *** Appends a properly formatted XYPair list to the specified StringBuffer
    **/
    public static StringBuffer ToListString(XYPair xyList[], StringBuffer sb)
    {
        return XYPair.ToListString(xyList,true,null,null,sb);
    }

    /** 
    *** Appends a properly formatted XYPair list to the specified StringBuffer
    **/
    public static StringBuffer ToListString(XYPair xyList[], boolean inclBrackets, String xFmt, String yFmt, StringBuffer sb)
    {
        if (sb == null) { sb = new StringBuffer(); }
        if (inclBrackets) {
            sb.append(LIST_START[0]);
        }
        if (xyList != null) {
            for (int i = 0; i < xyList.length; i++) {
                if (i > 0) { sb.append(LIST_SEPARATOR_CHAR); }
                xyList[i].toString(sb, xFmt, yFmt);
            }
        }
        if (inclBrackets) {
            sb.append(LIST_END[0]);
        }
        return sb;
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    public static void main(String argv[])
    {
        RTConfig.setCommandLineArgs(argv);
        XYPair xy[] = ParseXYPair(RTConfig.getString("xy",null));
        Print.sysPrintln("XYPair List: " + ToListString(xy));
    }

}
