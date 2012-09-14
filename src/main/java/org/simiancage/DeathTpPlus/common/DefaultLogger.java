package org.simiancage.DeathTpPlus.common;
/**
 *
 * PluginName: DeathTpPlus
 * Class: DefaultLogger
 * User: DonRedhorse
 * Date: 10.11.11
 * Time: 14:13
 *
 */

import org.bukkit.plugin.Plugin;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The DefaultLogger Class allows you to log in an easy way to the craftbukkit console.
 * It supports different log levels:<p>
 * {@link #info(String) info} <p>
 * {@link #warning(String) warning} <p>
 * {@link #severe(String) severe} <p>
 * {@link #debug(String, Object) debug} <p>
 * {@link #informational(String) informational} <p>
 * {@link #log(java.util.logging.Level, String, Throwable) log}<p>
 * and checks with the {@link ConfigManager} if debug and informational logging is
 * enabled.<p>
 * It also supports a plugin {@link #enableMsg()} and {@link #disableMsg()}.<p>
 * Initialization of the DefaultLogger is being done by {@link #getInstance(String, String)}  getInstance} .<p>
 * Based on the work of xZise.
 *
 * @author Don Redhorse
 * @author xZise
 */
public class DefaultLogger {
    /**
     * Reference to the logger
     */
    private final Logger logger;
    /**
     * contains the plugin name
     */
    private final String pluginName;
    /**
     * contains the plugin pluginVersion
     */
    private final String pluginVersion;
    /**
     * Instance of the DefaultLogger
     */
    private static DefaultLogger instance = null;


    /**
     * Instance of the ConfigurationCalls
     */
    private final ConfigManager config = ConfigManager.getInstance();


    /**
     * Method to get the instance of the DefaultLogger.
     * DefaultLogger will be initialized when necessary.
     *
     * @param loggerName should be "Minecraft"
     * @param pluginName the pluginname as string
     *
     * @return instance of the DefaultLogger
     */
    public static DefaultLogger getInstance(String loggerName, String pluginName) {
        if (instance == null) {
            instance = new DefaultLogger(loggerName, pluginName);
        }
        return instance;
    }


    /**
     * Method to get the instance of the DefaultLogger.
     * DefaultLogger will be initialized when necessary.
     *
     * @param pluginName the pluginname as string
     *
     * @return instance of the DefaultLogger
     */
    public static DefaultLogger getInstance(String pluginName) {
        if (instance == null) {
            instance = new DefaultLogger(pluginName);
        }
        return instance;
    }


    /**
     * Method to get the instance of the DefaultLogger.
     * DefaultLogger will be initialized when necessary.
     * Use this to initialize the DefaultLogger.
     *
     * @param pluginName the pluginname as Plugin
     *
     * @return instance of the DefaultLogger
     */
    public static DefaultLogger getInstance(Plugin pluginName) {
        if (instance == null) {
            instance = new DefaultLogger(pluginName);
        }
        return instance;
    }


    /**
     * Method to get the instance of the DefaultLogger.
     * Output to console via System.out.print if this method is called
     * when instance is NULL
     *
     * @return instance of the DefaultLogger, NOTE: This can be NULL
     */
    public static DefaultLogger getLogger() {
        return instance;
    }

    /**
     * Constructor to initialize the DefaultLogger via LoggerName and PluginName.
     * will hand over to {@link #DefaultLogger(java.util.logging.Logger, String, String)}
     *
     * @param loggerName should be "Minecraft"
     * @param pluginName the name of the plugin
     */
    private DefaultLogger(String loggerName, String pluginName) {
        this(Logger.getLogger(loggerName), pluginName, "");
    }


    /**
     * Constructor to initialize the DefaultLogger via the PluginName.
     * will hand over to {@link #DefaultLogger(String, String)}
     *
     * @param pluginName the name of the plugin
     */
    private DefaultLogger(String pluginName) {
        this("Minecraft", pluginName);
    }


    /**
     * Constructor which finally initializes the DefaultLogger.
     *
     * @param logger     Logger object of Minecraft
     * @param pluginName the name of the plugin
     * @param version    the version of the plugin
     */

    private DefaultLogger(Logger logger, String pluginName, String version) {
        this.logger = logger;
        this.pluginName = pluginName;
        this.pluginVersion = version;
    }


    /**
     * Constructor to initialize the DefaultLogger via a Plugin Object.
     * will hand over to {@link #DefaultLogger(java.util.logging.Logger, String, String)}
     *
     * @param plugin the plugin object
     */
    private DefaultLogger(Plugin plugin) {
        this(plugin.getServer().getLogger(), plugin.getDescription().getName(), plugin.getDescription().getVersion());
    }

// Nothing to change from here on....
// ***************************************************************************************************************************    

    /**
     * will output with INFO level to console if debugging is enabled and also prints out the object contents.
     *
     * @param msg    message to output
     * @param object object to output, will use .toString()
     */
    public void debug(String msg, Object object) {
        if (config.isDebugLogEnabled()) {
            String objectMsg;
            if (object == null) {
                objectMsg = "null";
            } else {
                objectMsg = object.toString();
            }
            this.logger.info(this.formatMessage(msg + "= [" + objectMsg + "]"));
        }
    }

    /**
     * will output with INFO level to console if debugging is enabled.
     *
     * @param msg message to output
     */
    public void debug(String msg) {
        if (config.isDebugLogEnabled()) {
            this.logger.info(this.formatMessage(msg));
        }
    }

    /**
     * formats the message by adding the [PluginName] in front.
     *
     * @param message to format, e.g. this is a test
     *
     * @return formated message, e.g. [PluginName] this is a test
     */
    private String formatMessage(String message) {
        return "[" + pluginName + "] " + message;
    }

    /**
     * will output with INFO level to console if informational logging is enabled
     *
     * @param msg message to output
     */
    public void info(String msg) {
        this.logger.info(this.formatMessage(msg));
    }

    /**
     * will output with WARN level to console
     *
     * @param msg message to output
     */
    public void warning(String msg) {
        this.logger.warning(this.formatMessage(msg));
    }

    /**
     * will output with SEVERE level to console
     *
     * @param msg message to output
     */
    public void severe(String msg) {
        this.logger.severe(this.formatMessage(msg));
    }

    /**
     * will output with SEVERE level to console and also prints the exception
     *
     * @param msg       message to output
     * @param exception exception to output
     */
    public void severe(String msg, Throwable exception) {
        this.log(Level.SEVERE, this.formatMessage(msg), exception);
    }

    /**
     * will output with specified level to console
     *
     * @param level     LoggerLevel = INFO, WARNING, SEVERE
     * @param msg       message to output
     * @param exception exception to output
     */
    public void log(Level level, String msg, Throwable exception) {
        this.logger.log(level, this.formatMessage(msg), exception);
    }

    /**
     * will output with WARNING level to console and also prints exception
     *
     * @param msg       message to output
     * @param exception exception to output
     */
    public void warning(String msg, Throwable exception) {
        this.log(Level.WARNING, this.formatMessage(msg), exception);
    }

    /**
     * will output [PluginName] v "VersionNumber" enabled to console
     */
    public void enableMsg() {
        this.info("v" + this.pluginVersion + " enabled");
    }

    /**
     * will output [PluginName] v "VersionNumber" disabled to console
     */
    public void disableMsg() {
        this.info("v" + this.pluginVersion + " disabled");
    }

    /**
     * will output with INFO level to console if informational logging is enabled
     *
     * @param msg message to output
     */
    public void informational(String msg) {
        if (config.isInformationLogEnabled()) {
            this.logger.info(formatMessage(msg));
        }
    }

}

