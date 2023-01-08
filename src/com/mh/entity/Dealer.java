package com.mh.entity;

import com.mh.crypto.CryptoEngine;
import com.mh.crypto.Parameters;
import com.mh.exception.IllegalAccessToCryptoModule;
import com.mh.exception.UnableToGeneratePrime;
import com.mh.utils.Constants;
import com.mh.utils.Utility;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;

/**
 * @author Munawar Hasan <munawar.hasan@nist.gov>
 */
public class Dealer {
    private int l;
    private int k;
    private int delta;

    private BigInteger p, q, p_prime, q_prime, n, m, e, d;
    private BigInteger v;
    private BigInteger[] polynomialCoefficients;

    private MessageDigest messageDigest;

    public Dealer(int l, int k){
        this.l = l; this.k = k;
        polynomialCoefficients = new BigInteger[k-1];
    }

    public void setUp() throws IllegalAccessToCryptoModule, UnableToGeneratePrime {
        CryptoEngine cryptoEngine = CryptoEngine.getCryptoEngine(this);
        p  = cryptoEngine.generateSafePrime(new Parameters().getPrimeSize(this));
        q  = cryptoEngine.generateSafePrime(new Parameters().getPrimeSize(this));

        p_prime = p.subtract(BigInteger.ONE).divide(BigInteger.TWO);
        q_prime = q.subtract(BigInteger.ONE).divide(BigInteger.TWO);

        n = p.multiply(q);
        m = p_prime.multiply(q_prime);
        e = new BigInteger(Constants.PUBLIC_EXPONENT);
        d = e.modInverse(m);

        for (int i = 0; i < k-1; i++)
            polynomialCoefficients[i] = new Utility().genBigIntegerInRange(m.subtract(BigInteger.ONE));


        while (true){
            v = new Utility().genBigIntegerInRange(n.subtract(BigInteger.ONE));
            if(v.gcd(n).compareTo(BigInteger.ONE) == 0) {
                v = v.multiply(v).mod(n);
                break;
            }else {
                v = new Utility().genBigIntegerInRange(n.subtract(BigInteger.ONE));
            }
        }

        delta = factorial(l);

        // SHA256 instance
        messageDigest = cryptoEngine.getHashingAlgorithm();
    }

    public void requestSecretShare(Player player) {
        player.setSecretShare(eval(player.enumeration));
    }

    public void requestVerificationKey(Player player) {
        BigInteger x = v.modPow(player.getSecretKey(), n);
        player.setVerificationKey(x);
    }

    public void setPublicParameters(Player player){
        player.setPublicParameters(n, v, BigInteger.valueOf(delta), messageDigest);
    }

    public void combine_signatures(List<Player> playerList, List<Integer> S){

        BigInteger message_hash = null;
        byte[] sha256 = messageDigest.digest(Constants.MESSAGE.getBytes());
        message_hash = new BigInteger(sha256).mod(n);

        // check proof of correctness for each player
        for (Integer i: S){

            BigInteger c = playerList.get(i-1).proofOfCorrectness.c;
            BigInteger z = playerList.get(i-1).proofOfCorrectness.z;

            BigInteger dealer_v_prime = v.modPow(z, n).multiply(playerList.get(i-1).verificationKey.modPow(BigInteger.ZERO.subtract(c), n)).mod(n);

            BigInteger dealer_x_tilde = message_hash.modPow(BigInteger.valueOf(4).multiply(BigInteger.valueOf(delta)), n);

            BigInteger temp = playerList.get(i-1).signature.sigma.modPow(BigInteger.ZERO.subtract(BigInteger.TWO.multiply(c)), n);
            BigInteger dealer_x_prime = dealer_x_tilde.modPow(z, n).multiply(temp).mod(n);

            BigInteger sigma_sq = playerList.get(i-1).signature.sigma.modPow(BigInteger.TWO, n);

            String dealer_poc = v.toString()
                    .concat(dealer_x_tilde.toString())
                    .concat(playerList.get(i-1).verificationKey.toString())
                    .concat(sigma_sq.toString())
                    .concat(dealer_v_prime.toString())
                    .concat(dealer_x_prime.toString());

            BigInteger dealer_c = new BigInteger(messageDigest.digest(dealer_poc.getBytes())).mod(n);
            if (c.compareTo(dealer_c) == 0){
                System.out.println(playerList.get(i-1).name +": Proof Of Correctness is Valid");
            }else
                System.out.println(playerList.get(i-1).name +": Proof Of Correctness is Invalid!!!");
        }

        // combine signatures

        HashMap<Integer, Integer> lagrangePoly = new HashMap<>();
        for (int i = 0; i < S.size(); i++){
            int num = 1;
            int denom = 1;
            for (int j = 0; j < S.size(); j++){
                if (i == j)
                    continue;
                num = num * (-S.get(j));
                denom = denom * (S.get(i) - S.get(j));
            }
            num = delta * num;
            lagrangePoly.put(S.get(i), (num / denom));
        }

        BigInteger w = BigInteger.ONE;
        for (int i = 0; i < S.size(); i++)
            w = w.multiply(playerList.get(S.get(i) - 1).signature.sigma.modPow(BigInteger.valueOf(2 * lagrangePoly.get(S.get(i))), n)).mod(n);


        BigInteger e_prime = BigInteger.valueOf(4).multiply(BigInteger.valueOf(delta).pow(2));

        BigInteger _w = w.modPow(e, n);
        BigInteger _x = message_hash.modPow(e_prime, n);

        if (_w.compareTo(_x) != 0)
            System.out.println("Unexpected Error\n");

        //xgcd start
        BigInteger b = e;
        BigInteger s = BigInteger.ZERO;
        BigInteger r = b;
        BigInteger old_s = BigInteger.ONE;
        BigInteger old_r = e_prime;
        BigInteger bezout_t = null;

        while (r.compareTo(BigInteger.ZERO) != 0){
            BigInteger quotient = old_r.divide(r);

            BigInteger temp = r;
            r = old_r.subtract(quotient.multiply(r));
            old_r = temp;

            temp = s;
            s = old_s.subtract(quotient.multiply(s));
            old_s = temp;
        }

        if (b.compareTo(BigInteger.ZERO) != 0)
            bezout_t = old_r.subtract(old_s.multiply(e_prime)).divide(b);
        else
            bezout_t = BigInteger.ZERO;

        System.out.println("Bézout coefficients: (" +old_s +", " +bezout_t +")");
        //xgcd end

        //y = w^a * x^b
        BigInteger y = (w.modPow(old_s, n).multiply(message_hash.modPow(bezout_t, n))).mod(n);
        //y = _y.mod(n);

        // y^e
        BigInteger y_e = y.modPow(e, n);

        System.out.println("y_e:\t" +y_e);
        System.out.println("H(M):\t" +message_hash);

        if (y_e.compareTo(message_hash) == 0)
            System.out.println("SUCCESS!");
        else
            System.out.println("FAILED!");
    }

    private BigInteger eval(int enumeration){
        BigInteger b = BigInteger.valueOf(enumeration);
        BigInteger value = BigInteger.ZERO;
        for (int deg = 1; deg < k; deg++)
            value = value.add(polynomialCoefficients[deg-1].multiply(b.pow(deg)));
        return value.add(d).mod(m);
    }

    private int factorial(int i){
        if (i == 0)
            return 1;
        else
            return i * factorial(i-1);
    }

}
