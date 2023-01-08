package com.mh.exception;

/**
 * @author Munawar Hasan <munawar.hasan@nist.gov>
 */
public class ThresholdMismatch extends Exception{
    public ThresholdMismatch(){
        super(ExceptionMessage.THRESHOLDMISMATCH);
    }
}
