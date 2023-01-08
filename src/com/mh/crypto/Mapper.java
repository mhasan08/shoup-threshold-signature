package com.mh.crypto;

/**
 * @author Munawar Hasan <munawar.hasan@nist.gov>
 */
public class Mapper {
    protected boolean map(Object o, ENTITIES entities){
        return o.getClass().getName().toLowerCase().substring(o.getClass().getPackageName().length() + 1).equals(entities.name().toLowerCase());
    }
    protected boolean map(String s1, String s2){
        return s1.toLowerCase().equals(s2.toLowerCase());
    }
}
