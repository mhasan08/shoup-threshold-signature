package com.mh.exception;

/**
 * @author Munawar Hasan <munawar.hasan@nist.gov>
 */
public class IllegalAccessToCryptoModule extends Exception {
    public IllegalAccessToCryptoModule(){super(ExceptionMessage.ILLEGALCRYTOMODULEACCESS);}
}
