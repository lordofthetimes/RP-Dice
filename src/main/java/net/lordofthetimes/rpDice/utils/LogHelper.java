package net.lordofthetimes.rpDice.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogHelper {
    public final Logger logger;

    public LogHelper(Logger logger){
        this.logger = logger;
    }

    public void logInfo(String message){
        logger.log(Level.INFO,message);
    }

    public void logWarn(String message){
        logger.log(Level.WARNING,message);
    }

    public void logError(String message, Throwable e){
        logger.log(Level.SEVERE, message, e);
    }
    public void logError(String message){
        logger.log(Level.SEVERE, message);
    }
}
