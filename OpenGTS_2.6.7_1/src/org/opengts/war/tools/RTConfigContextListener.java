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
// References:
//  - http://livedocs.macromedia.com/jrun/4/Programmers_Guide/servletlifecycleevents3.htm
// ----------------------------------------------------------------------------
// Change History:
//  2007/01/25  Martin D. Flynn
//     -Initial release
//  2009/01/28  Martin D. Flynn
//     -Added support for monitoring HttpSessions
//     -Servlet context properties now initialized via DBConfig.servletInit(...)
//  2015/05/03  Martin D. Flynn
//     -Added "GetSessionForID" [2.5.8-B11]
//  2020/02/19  GTS Development Team 
//     -Added "Session Logout" logging information [2.6.7-B19b]
// ----------------------------------------------------------------------------
package org.opengts.war.tools;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.opengts.util.*;

import org.opengts.Version;
import org.opengts.db.*;
import org.opengts.dbtools.DBConnection;

/**
*** This class initializes and loads the servlet configuration properties into the Runtime configuration
*** class RTConfig.  A reference to this class is typically placed in the Servlet 'web.xml' file as follows:<br>
*** <pre>
***   &lt;listener&gt;
***       &lt;listener-class&gt;org.opengts.war.tools.RTConfigContextListener&lt;/listener-class&gt;
***   &lt;/listener&gt;
*** </pre>
**/

public class RTConfigContextListener
    implements ServletContextListener, 
               ServletRequestListener,
               HttpSessionListener,
               HttpSessionActivationListener
{

    /* prevent RememberMe re-login on forced logout */
    private static final boolean NoRememberMeOnForcedLogout = true; // 'true' for production

    // ------------------------------------------------------------------------

    /* must match "org.opengts.war.track.Constants.*" */
    public static final String PARM_ACCOUNT                 = "account";
    public static final String PARM_USER                    = "user";
    public static final String PARM_IP_ADDR                 = "ipAddr";

    /* local session variables ("setAttribute") */
    public static final String PARM_SessionCreationTime     = "sessionTime";
    public static final String PARM_RTConfigContextListener = "RTConfigContextListener";

    // ------------------------------------------------------------------------

    public static final String PROP_DebugMode[] = new String[] { 
        "debugMode", 
        "debug" 
    };

    public static final String PROP_DBConfig_init[] = new String[] { 
        "DBConfig.init", 
        "DBConfig.servletInit" 
    };

    // ------------------------------------------------------------------------

    public static final Object ServletLock = new Object();

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
    *** LoginSession
    **/
    public static class LoginSession
    {
        private String accountID  = null;
        private String userID     = null;
        private long   loginTime  = 0L;
        private String ipAddress  = null;
        private String sessionID  = null;
        public LoginSession(
            String acctID, String usrID, long logTime, 
            String ipAddr,
            String sessID) {
            this.accountID = acctID;
            this.userID    = usrID;
            this.loginTime = logTime;
            this.ipAddress = ipAddr;
            this.sessionID = sessID;
        }
        public String getAccountID() {
            return this.accountID;
        }
        public String getUserID() {
            return this.userID;
        }
        public long getLoginTime() {
            return this.loginTime;
        }
        public String getIpAddress() {
            return this.ipAddress;
        }
        public String getSessionID() {
            return this.sessionID;
        }
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    public interface HttpSessionFilter
    {
        public boolean filterSession(HttpSession session);
    }

    /**
    *** Returns the current number of sessions
    *** @param sc  The ServletContext
    *** @return The current number of sessions
    **/
    public static int GetSessionCount(ServletContext sc)
    {
        return RTConfigContextListener.GetSessionCount(sc, null);
    }

    /**
    *** Returns the current number of sessions matching the specified filter
    *** @param sc       The ServletContext
    *** @param filter   The HttpSession filter (total session counter returned if filter is null)
    *** @return The current number of sessions matching the specified filter
    **/
    public static int GetSessionCount(ServletContext sc, HttpSessionFilter filter)
    {
        RTConfigContextListener rccl = (sc != null)? (RTConfigContextListener)sc.getAttribute(PARM_RTConfigContextListener) : null;
        if (rccl != null) {
            return rccl.getSessionCount(filter);
        } else {
            Print.logWarn("RTConfigContextListener not found!");
            return -1;
        }
    }

    // ------------------------------------------------------------------------

    /**
    *** Gets the HttpSession for the specified id (JSESSIONID)
    *** @param sc  The ServletContext
    *** @param id  The JSESSIONID
    *** @return The HttpSession, or null if the JSESSIONID was not found
    **/
    public static HttpSession GetSessionForID(ServletContext sc, String id)
    {
        RTConfigContextListener rccl = (sc != null)? (RTConfigContextListener)sc.getAttribute(PARM_RTConfigContextListener) : null;
        if (rccl != null) {
            return rccl.getSessionForID(id);
        } else {
            Print.logWarn("RTConfigContextListener not found!");
            return null;
        }
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    private static RTConfigContextListener  rtCfgListener     = null;

    // ------------------------------------------------------------------------

    /**
    *** Add shutdown callback 
    **/
    public static void addCallbackOnContextDestroyed(Runnable sdcb)
    {
        if (RTConfigContextListener.rtCfgListener != null) {
            if (RTConfigContextListener.rtCfgListener.shutdownCallbacks == null) {
                RTConfigContextListener.rtCfgListener.shutdownCallbacks = new Vector<Runnable>();
            }
            RTConfigContextListener.rtCfgListener.shutdownCallbacks.add(sdcb);
        } else {
            Print.logWarn("RTConfigContextListener not yet initialized!");
        }
    }

    // ------------------------------------------------------------------------

    private int                             sessionCount      = 0;
    private Map<String,HttpSession>         sessionMap        = Collections.synchronizedMap(new HashMap<String,HttpSession>());

    private Vector<Runnable>                shutdownCallbacks = null;
    
    private Set<String>                     forcedLogoutAccts = new HashSet<String>();

    /**
    *** Constructor
    **/
    public RTConfigContextListener() 
    {
        if (RTConfigContextListener.rtCfgListener == null) {
            RTConfigContextListener.rtCfgListener = this;
        } else {
            Print.logWarn("RTConfigContextListener singleton already set!");
        }
    }

    // ------------------------------------------------------------------------
    // ServletContextListener interface

    /**
    *** This method is called by the Servlet container when the Servlet context is initialized
    *** @param sce  A ServletContextEvent instance
    **/
    public void contextInitialized(ServletContextEvent sce)
    {
        ServletContext srvCtx = sce.getServletContext();
        // -- ServletContext also provides the following logging methods:
        // -    log(String msg);
        // -    log(String msg, Throwable th);

        // --------------------------------------------------------------------
        // -- Note: Log output occurring in this method may appear in the Tomcat
        // -  'catalina.out' file, regardless of the 'log.file.enable' specified state.

        /* current Version/GTS_HOME value */
        Print.logInfo("Version     : " + Version.getVersion());

        /* current Version/GTS_HOME value */
        String GTS_HOME = System.getenv(DBConfig.env_GTS_HOME);
        Print.logInfo("GTS_HOME    : " + StringTools.blankDefault(GTS_HOME,"not defined"));

        /* context name */
        String srvCtxName = StringTools.trim(srvCtx.getServletContextName()); // "display-name" tag
        if (!StringTools.isBlank(srvCtxName)) {
            Print.logInfo("Context Name: " + srvCtxName);
            if (srvCtxName.startsWith("dcs.") || srvCtxName.startsWith("dcs:")) {
                String dcsName = srvCtxName.substring(4).trim();
                RTConfig.setContextName(dcsName);
                DCServerFactory.SetSpecificDCServerName(dcsName);
            } else
            if (srvCtxName.startsWith("dcs_") || srvCtxName.startsWith("dcs-")) {
                RTConfig.setContextName(srvCtxName);
                String dcsName = srvCtxName.substring(4).trim();
                DCServerFactory.SetSpecificDCServerName(dcsName);
            } else {
                RTConfig.setContextName(srvCtxName);
            }
            // -- the context name is typically referenced in 'webapps.conf' to set the log file name
        } else {
            // -- context name is blank
            Print.logWarn("Context Name: not defined");
        }

        /* context path */
        String srvCtxPath = StringTools.trim(srvCtx.getRealPath(""));
        if (!StringTools.isBlank(srvCtxPath)) {
            Print.logInfo("Context Path: " + srvCtxPath);
            RTConfig.setContextPath(srvCtxPath);
        } else {
            // -- context path is blank
            Print.logWarn("Context Path: not defined [name: " + srvCtxName + "]");
        }

        // --------------------------------------------------------------------

        /* initialize Servlet context properties */
        boolean dbConfigInit = true;
        Properties srvCtxProps = new Properties();
        Enumeration<?> parmEnum = srvCtx.getInitParameterNames();
        for (;parmEnum.hasMoreElements();) {
            String key = parmEnum.nextElement().toString();
            String val = srvCtx.getInitParameter(key);
            if (val != null) {
                Print.logInfo("Adding Servlet property: " + key + " ==> " + val);
                srvCtxProps.setProperty(key, val);
                if (ListTools.contains(PROP_DebugMode,key)) {
                    boolean debugMode = StringTools.parseBoolean(val,true);
                    RTConfig.setDebugMode(debugMode);
                    if (debugMode) {
                        Print.setLogLevel(Print.LOG_ALL);               // log everything
                        Print.setLogHeaderLevel(Print.LOG_ALL);         // include log header on everything
                    } else {
                        // -- leave as-is
                    }
                } else
                if (ListTools.contains(PROP_DBConfig_init,key)) {
                    // -- override to disable "DBConfig.servletInit(...)" ...
                    dbConfigInit = StringTools.parseBoolean(val,true);
                }
            }
        }
        if (dbConfigInit) {
            DBConfig.servletInit(srvCtxProps);
        } else {
            Print.logInfo("Skipping 'DBConfig.servletInit' ...");
            RTConfig.setServletContextProperties(srvCtxProps);
            if (RTConfig.isDebugMode()) {
                Print.setLogLevel(Print.LOG_ALL);                       // log everything
                Print.setLogHeaderLevel(Print.LOG_ALL);                 // include log header on everything
            }
        }
        //Print.logInfo("RTConfig Main Command: " + RTConfig.getString(RTKey.MAIN_COMMAND,"?"));

        // --------------------------------------------------------------------
        // -- TODO: init Freemarker

        // --------------------------------------------------------------------

        /* save this RTConfigContextListener in the ServletContext */
        srvCtx.setAttribute(PARM_RTConfigContextListener, this);

        /* java.awt.headless */
        String headless = System.getProperty("java.awt.headless","false");
        Print.logInfo("java.awt.headless=" + headless);

        /* display where the log output is being sent to */
        File logFile = Print.getLogFile();
        if (logFile != null) {
            Print.sysPrintln("["+srvCtxName+"] Logging to file: " + logFile);
        } else {
            Print.sysPrintln("["+srvCtxName+"] Logging to default location");
        }

    }

    /**
    *** This method is called by the Servlet container when the Servlet context is destroyed
    *** @param sce  A ServletContextEvent instance
    **/
    public void contextDestroyed(ServletContextEvent sce)
    {
        ServletContext srvCtx = sce.getServletContext();
        String srvCtxName = srvCtx.getServletContextName();

        /* clear server context properties */
        //RTConfig.clearServletContextProperties(null);

        /* close all open DBConnections */
        DBConnection.closeAllConnections();

        /* stop all active pool threads */
        int preThreadCount = ThreadPool.GetTotalThreadCount();
        ThreadPool.StopThreads(true/*now*/);
        try { Thread.sleep(1000); } catch (Throwable t) {}
        int postThreadCount = ThreadPool.GetTotalThreadCount();
        Print.logInfo("Remaining pool threads [" + preThreadCount + "]: " + postThreadCount);
        
        /* shutdown callbacks */
        if (!ListTools.isEmpty(this.shutdownCallbacks)) {
            for (Runnable r : this.shutdownCallbacks) {
                try {
                    r.run();
                } catch (Throwable th) {
                    // -- ignore
                }
            }
        }

        /* context destroyed */
        Print.logInfo("... Servlet Context destroyed: " + srvCtxName);
        Print.logInfo("-----------------------------------------------");

    }

    // ------------------------------------------------------------------------
    // ServletRequestListener interface

    /**
    *** Callback by the Servlet container when a requests comes into scope
    *** @param sre  The ServletRequestEvent
    **/
    public void requestInitialized(ServletRequestEvent sre)
    {
        HttpServletRequest request = (HttpServletRequest)sre.getServletRequest();
        HttpSession        hs      = request.getSession();
        String             hid     = hs.getId();
        if (hs.isNew()) {
            //Print.logInfo("New Request Init: " + hid);
        } else {
            //Print.logInfo("Old Request Init: " + hid);
        }
    }

    /**
    *** Callback by the Servlet container when a requests leaves scope
    *** @param sre  The ServletRequestEvent
    **/
    public void requestDestroyed(ServletRequestEvent sre)
    {
        // -- NO-OP
        //Print.logInfo("Request Detroyed ...");
    }

    // ------------------------------------------------------------------------
    // HttpSessionListener interface

    /**
    *** Callback by the Servlet container when a new HttpSession is created.
    *** NOTE: Does NOT get called when Tomcat is restarted (so "sessionMap" does not get rebuilt).
    *** @param hse  The HttpSessionEvent
    **/
    public void sessionCreated(HttpSessionEvent hse) 
    {
        HttpSession hs  = hse.getSession();
        String      hid = hs.getId();
        // -- remove session from map
        int sessCnt;
        synchronized (this.sessionMap) {
            hs.setAttribute(PARM_SessionCreationTime, new Long(DateTime.getCurrentTimeSec()));
            this.sessionCount++; // may be just a login screen
            this.sessionMap.put(hid, hs);
            sessCnt = this.sessionCount;
        }
        //Print.logInfo("Session Created: " + hid + " [" + sessCnt + "]");
    }

    /**
    *** Callback by the Servlet container when a HttpSession is destroyed
    *** @param hse  The HttpSessionEvent
    **/
    public void sessionDestroyed(HttpSessionEvent hse) 
    {
        HttpSession hs  = hse.getSession();
        String      hid = hs.getId();
        // -- remove session from map
        int sessCnt;
        synchronized (this.sessionMap) {
            if (this.sessionCount > 0) { this.sessionCount--; }
            this.sessionMap.remove(hid);
            sessCnt = this.sessionCount;
        }
        // -- display logout 
        //Print.logInfo("Session Detroyed: " + hid + " [" + sessCnt + "]");
        String _acctID  = StringTools.trim(        hs.getAttribute(PARM_ACCOUNT            ));
        String _userID  = StringTools.trim(        hs.getAttribute(PARM_USER               ));
        long   _loginTS = StringTools.parseLong(   hs.getAttribute(PARM_SessionCreationTime),0L);
        String _ipAddr  = StringTools.blankDefault(hs.getAttribute(PARM_IP_ADDR            ),"?");
        if (!StringTools.isBlank(_acctID) || !StringTools.isBlank(_userID)) { //  [2.6.7-B19b]
            long dSec = (_loginTS > 0L)? (DateTime.getCurrentTimeSec() - _loginTS) : -1L;
            String dHMS = (dSec > 0L)? StringTools.formatElapsedSeconds(dSec,StringTools.ELAPSED_FORMAT_HHMMSS) : "n/a";
            Print.logInfo("Session Logout: Account="+_acctID + " User="+_userID+" LoginTime="+dSec+"["+dHMS+"] IPAddr="+_ipAddr);
        }
    }

    // ------------------------------------------------------------------------
    // HttpSessionActivationListener interface (needs to be bound to a Session?)

    /**
    *** Callback by the Servlet container when an HttpSession is activated.
    *** @param hse  The HttpSessionEvent
    **/
    public void sessionDidActivate(HttpSessionEvent hse)
    {
        HttpSession    hs  = hse.getSession();
        String         hid = hs.getId();
        //Print.logInfo("Session did activate: " + hid);
    }

    /**
    *** Callback by the Servlet container when an HttpSession will be passivated.
    *** @param hse  The HttpSessionEvent
    **/
    public void sessionWillPassivate(HttpSessionEvent hse)
    {
        HttpSession    hs  = hse.getSession();
        String         hid = hs.getId();
        //Print.logInfo("Session will passivate: " + hid);
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
    *** Gets the HttpSession for the specified id (JSESSIONID)
    *** @param id  The JSESSIONID
    *** @return The HttpSession, or null if the JSESSIONID was not found
    **/
    public HttpSession getSessionForID(String id)
    {
        if (StringTools.isBlank(id)) {
            return null;
        } else {
            HttpSession hs = null;
            synchronized (this.sessionMap) {
                hs = this.sessionMap.get(id); // may be null
            }
            return hs;
        }
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
    *** Returns a list of current HttpSession instances
    *** @return A list of HttpSession instances (non-null)
    **/
    public Collection<HttpSession> getSessions()
    {
        Collection<HttpSession> list = null;
        synchronized (this.sessionMap) {
            list = new Vector<HttpSession>(this.sessionMap.values());
        }
        return list;
    }

    /**
    *** Returns the current number of open sessions
    *** @param filter  The HttpSession filter
    *** @return The current number of open sessions
    **/
    public int getSessionCount(HttpSessionFilter filter)
    {
        int count = 0;
        synchronized (this.sessionMap) {
            if (filter == null) {
                count = this.sessionCount;
            } else {
                //Print.logInfo("Session map size: %d", this.sessionMap.size());
              //for (HttpSession hs : this.sessionMap.values()) [
                for (String id : this.sessionMap.keySet()) {
                    HttpSession hs = this.sessionMap.get(id);
                    if (hs == null) {
                        // -- should not occur
                    } else
                    if (filter.filterSession(hs)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
    *** Returns a list of current open sessions (LoginSession)
    *** @param accountID  The AccountID filter.  If non-blank, only matching Account
    ***                 LoginSessions will be returned
    *** @return The list of current open sessions
    **/
    public java.util.List<LoginSession> getLoginSessions(String accountID)
    {
        java.util.List<LoginSession> loginList = new Vector<LoginSession>();
        synchronized (this.sessionMap) {
            for (String id : this.sessionMap.keySet()) {
                HttpSession hs = this.sessionMap.get(id);
                if (hs == null) { continue; } // will not occur
                String _acctID  = (String)hs.getAttribute(PARM_ACCOUNT); // may be null
                String _userID  = (String)hs.getAttribute(PARM_USER);    // may be null
                long   _loginTS = StringTools.parseLong(hs.getAttribute(PARM_SessionCreationTime),0L);
                String _ipAddr  = (String)hs.getAttribute(PARM_IP_ADDR); // may be null
                if (StringTools.isBlank(_acctID)) {
                    // -- skip entry with no Account-ID
                } else 
                if (!StringTools.isBlank(accountID) && !accountID.equalsIgnoreCase(_acctID)) {
                    // -- skip entry that does not match requested account
                } else {
                    // -- save this entry
                    loginList.add(new LoginSession(_acctID,_userID,_loginTS,_ipAddr,id));
                }
            }
        }
        return loginList;
    }

    // ------------------------------------------------------------------------

    /** 
    *** Returns true if the specified AccountID has previously been forced to log-out.
    *** The account forced-logout-state will be cleared after tested.
    **/
    public boolean hasForcedAccountLogout(String accountID)
    {
        boolean rtn = false;
        if (!StringTools.isBlank(accountID)) {
            synchronized (this.forcedLogoutAccts) {
                rtn = this.forcedLogoutAccts.remove(accountID); // true if it was present
            }
        }
        return rtn;
    }

    /**
    *** Returns the current number of sessions matching the specified filter
    *** @param sc       The ServletContext
    *** @param filter   The HttpSession filter (total session counter returned if filter is null)
    *** @return The current number of sessions matching the specified filter
    **/
    public static boolean HasForcedAccountLogout(ServletContext sc, String acctID)
    {
        RTConfigContextListener rccl = (sc != null)? (RTConfigContextListener)sc.getAttribute(PARM_RTConfigContextListener) : null;
        if (rccl != null) {
            return rccl.hasForcedAccountLogout(acctID);
        } else {
            Print.logWarn("RTConfigContextListener not found!");
            return false;
        }
    }

    // --------------------------------

    /**
    *** Force account logout (if logged in)
    **/
    public int forceAccountLogout(String accountID)
    {
        // -- must specify an accountID
        if (StringTools.isBlank(accountID)) {
            return 0;
        }
        // -- reworked to prevent "ConcurrentModificationException" [2.6.7-B21f]
        // -  even though 'this.sessionMap' was synchronized
        // -- ---------------------------------
        // -  java.util.ConcurrentModificationException
        // -    at java.util.HashMap$HashIterator.nextNode(HashMap.java:1445)
        // -    at java.util.HashMap$KeyIterator.next(HashMap.java:1469)
        // -    at org.opengts.war.tools.RTConfigContextListener.forceAccountLogout(RTConfigContextListener.java:621)
        // -    at org.opengts.war.tools.RTConfigContextListener.ForceAccountLogout(RTConfigContextListener.java:660)
        // -    at org.opengts.war.track.page.SysAdminAccounts.writePage(SysAdminAccounts.java:572)
        // -    ...
        // -- ---------------------------------
        int count = 0;
        Collection<HttpSession> sessionList = this.getSessions(); // non null (may be empty)
        for (HttpSession hs : sessionList) {
            if (hs == null) { continue; } // will not occur
            try {
                // -- matching accountID?
                String acctID = (String)hs.getAttribute(PARM_ACCOUNT); // may throw IllegalStateException
                if (!accountID.equalsIgnoreCase(acctID)) {
                    // -- skip entry that does not match requested account
                    continue;
                }
                // -- invalidate session
                Print.logInfo("Invalidating Account session (force logout): " + accountID);
                hs.invalidate(); // may throw IllegalStateException
                // -- Additional checking will be necessary if the user has selected RememberMe.
                // -- Would like to "AttributeTools.removeAllCookies(req,resp);", but cannot do this here
                if (NoRememberMeOnForcedLogout) {
                    // -- prevent RememberMe re-login on force-logout
                    synchronized (this.forcedLogoutAccts) {
                        // -- this is used iff user has selected RememberMe
                        this.forcedLogoutAccts.add(accountID); // Set (adds only one unique copy)
                    }
                }
                count++;
            } catch (IllegalStateException ise) {
                // -- already invalidated (they recently already logged out?)
                Print.logError("Account session already invalidated? " + accountID);
            }
        }
        return count;
    }

    /**
    *** Returns the current number of sessions matching the specified filter
    *** @param sc       The ServletContext
    *** @param filter   The HttpSession filter (total session counter returned if filter is null)
    *** @return The current number of sessions matching the specified filter
    **/
    public static int ForceAccountLogout(ServletContext sc, String acctID)
    {
        RTConfigContextListener rccl = (sc != null)? (RTConfigContextListener)sc.getAttribute(PARM_RTConfigContextListener) : null;
        if (rccl != null) {
            return rccl.forceAccountLogout(acctID);
        } else {
            Print.logWarn("RTConfigContextListener not found!");
            return -1;
        }
    }

    // ------------------------------------------------------------------------

}

    
