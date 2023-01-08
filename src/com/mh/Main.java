package com.mh;

import com.mh.entity.Dealer;
import com.mh.entity.Player;
import com.mh.exception.IllegalAccessToCryptoModule;
import com.mh.exception.ThresholdMismatch;
import com.mh.exception.UnableToGeneratePrime;
import com.mh.utils.Constants;
import com.mh.utils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Munawar Hasan <munawar.hasan@nist.gov>
 */
public class Main {
    static int l=6, k=4;
    public static void main(String[] args) throws ThresholdMismatch, IllegalAccessToCryptoModule, UnableToGeneratePrime { start(l, k);}

    private static void start(int ll, int kk) throws ThresholdMismatch, IllegalAccessToCryptoModule, UnableToGeneratePrime {
        int t = ll - kk;
        if ((kk >= t + 1) && (ll - t) >= kk){
            System.out.println("Total Parties: " +String.valueOf(ll));
            System.out.println("Participating Parties: " +String.valueOf(kk));
        }else {
            throw new ThresholdMismatch();
        }

        Dealer dealer = new Dealer(ll, kk);
        dealer.setUp();

        List<Player> playerList = new ArrayList<>();

        for (int i = 0; i < ll; i++) {
            Player player = new Player(i + 1, new Utility().generateRandomString(i+1));
            dealer.requestSecretShare(player);
            dealer.requestVerificationKey(player);
            dealer.setPublicParameters(player);

            playerList.add(player);
        }

        // generate signatures
        for (Player p: playerList)
            p.doSign(Constants.MESSAGE);


        List<Integer> S = new ArrayList<>();
        S.add(2);
        S.add(3);
        S.add(4);
        S.add(5);

        System.out.print("Participating Players: ");
        System.out.println(S);

        // combine signatures
        dealer.combine_signatures(playerList, S);

        System.out.println("<<completed>>");
    }
}
