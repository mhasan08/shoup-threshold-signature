package com.mh.crypto;

import com.mh.exception.IllegalAccessToCryptoModule;

/**
 * @author Munawar Hasan <munawar.hasan@nist.gov>
 */
public final class Parameters {
    private static final int PRIME_SIZE = 1024;
    //private static final int PRIME_SIZE = 2048;

    public int getPrimeSize(Object o) throws IllegalAccessToCryptoModule{
        if(!new Mapper().map(o, ENTITIES.DEALER))
            throw new IllegalAccessToCryptoModule();
        else
            return PRIME_SIZE;
    }

    class PRIME{
        int bits;
        String task;
        PRIME(int bits, String task){
            this.bits = bits;
            this.task = task;
        }

        public int getBits() {
            return bits;
        }

        public String getTask() {
            return task;
        }
    }
}
