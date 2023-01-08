package com.mh.crypto;

import com.mh.exception.IllegalAccessToCryptoModule;
import com.mh.exception.UnableToGeneratePrime;
import com.mh.utils.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

/**
 * @author Munawar Hasan <munawar.hasan@nist.gov>
 */
public class CryptoEngine {
    private static CryptoEngine cryptoEngine = new CryptoEngine();
    private CryptoEngine(){}

    public static CryptoEngine getCryptoEngine(Object o) throws IllegalAccessToCryptoModule{
        if(!new Mapper().map(o, ENTITIES.DEALER))
            throw new IllegalAccessToCryptoModule();
        else
            return cryptoEngine;
    }

    public BigInteger generateSafePrime(int bits) throws UnableToGeneratePrime {
        System.out.println("generating Sophie Germain prime ...");

        final String[] primeString = {null};
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Process process = Runtime.getRuntime().exec(createOpenSSLCMD(new Parameters().new PRIME(bits, "prime")));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    primeString[0] = reader.readLine();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (t1.isAlive()){
                    System.out.print("++++");
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

        t1.start();t2.start();

        try {
            t1.join();t2.join();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println();
        return new BigInteger(primeString[0]);
    }

    private String createOpenSSLCMD(Parameters.PRIME prime){
        //"openssl prime -bits 1024 -checks 64 -generate -safe"
        return "openssl " +prime.getTask() +" -bits " +prime.getBits() +" -checks 64 -generate -safe";
    }

    public MessageDigest getHashingAlgorithm(){
        MessageDigest messageDigest = null;
        try{
            messageDigest = MessageDigest.getInstance(Constants.HASHING_ALGORITHM);
        }catch (Exception e){
            e.printStackTrace();
        }
        return messageDigest;
    }
}
