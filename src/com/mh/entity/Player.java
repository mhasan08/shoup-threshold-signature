package com.mh.entity;

import com.mh.utils.Utility;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * @author Munawar Hasan <munawar.hasan@nist.gov>
 */
public class Player {
    public int enumeration;
    public String name;

    private BigInteger secretShare;
    public BigInteger verificationKey;
    private BigInteger v;

    private BigInteger n;
    private BigInteger delta;

    private MessageDigest messageDigest;

    public Signature signature;
    public ProofOfCorrectness proofOfCorrectness;

    public Player(int enumeration, String name){
        this.enumeration = enumeration;
        this.name = name;
    }
    public void setSecretShare(BigInteger s){
        this.secretShare = s;
    }

    public BigInteger getSecretKey(){
        return secretShare;
    }

    public void setVerificationKey(BigInteger verificationKey){
        this.verificationKey = verificationKey;
    }

    public void setPublicParameters(BigInteger n, BigInteger v, BigInteger delta, MessageDigest messageDigest){
        this.n = n;
        this.v = v;
        this.delta = delta;
        this.messageDigest = messageDigest;
    }

    public void doSign(String message){
        signature = new Signature();
        proofOfCorrectness = new ProofOfCorrectness();

        BigInteger message_hash = null;
        BigInteger sigma;

        // generate signature

        byte[] sha256 = messageDigest.digest(message.getBytes());
        message_hash = new BigInteger(sha256).mod(n);


        sigma = message_hash.modPow(BigInteger.TWO.multiply(delta).multiply(secretShare), n);

        signature.message = message;
        signature.sigma = sigma;

        // proof of correctness

        BigInteger x_tilde = message_hash.modPow(BigInteger.valueOf(4).multiply(delta), n);
        BigInteger r = new Utility().genBigIntegerInRange(n);

        BigInteger v_prime = v.modPow(r, n);
        BigInteger x_prime = x_tilde.modPow(r, n);

        BigInteger sigma_sq = sigma.modPow(BigInteger.TWO, n);

        String poc = v.toString()
                .concat(x_tilde.toString())
                .concat(verificationKey.toString())
                .concat(sigma_sq.toString())
                .concat(v_prime.toString())
                .concat(x_prime.toString());

        BigInteger c = new BigInteger(messageDigest.digest(poc.getBytes())).mod(n);
        BigInteger z = secretShare.multiply(c).add(r);

        proofOfCorrectness.c = c;
        proofOfCorrectness.z = z;
    }

    public class Signature{
        public String message;
        public BigInteger sigma;
    }

    public class ProofOfCorrectness{
        BigInteger z;
        BigInteger c;
    }
}
