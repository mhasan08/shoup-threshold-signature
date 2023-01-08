package com.mh.utils;

import java.math.BigInteger;
import java.util.Random;

/**
 * @author Munawar Hasan <munawar.hasan@nist.gov>
 */
public class Utility {
    public BigInteger genBigIntegerInRange(BigInteger range){
        int bitLength = range.bitLength();
        Random random = new Random();
        return new BigInteger(bitLength, random).mod(range);
    }
    public String generateRandomString(int i){
        return "P".concat(String.valueOf(i));
    }
}