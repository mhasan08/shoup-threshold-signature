# Java implementation of Shoup's Practical Threshold Signature

Implementation of Shoup's Practical Threshold Signatures (https://www.shoup.net/papers/thsig.pdf). 
<br/>

> The implementation uses two entities: a dealer ( $\mathcal{D}$ ) and players ( $\mathcal{P}$ ). $\mathcal{D}$ is the trusted entity. The code works as follows:
* $\mathcal{D}$ generates secret key and verification key for all the players 
* $\mathcal{P}$ signs the message and generates proof of correctness
* $\mathcal{D}$ verifies proof of correctness and the signature of each of the participating players

❗Implementation uses openSSL for generating the two Sophie Germain primes.

Output:
```
Total Parties: 6
Participating Parties: 4
generating Sophie Germain prime ...
++++++++
generating Sophie Germain prime ...
++++++++++++++++++++++++++++
Participating Players: [2, 3, 4, 5]
P2: Proof Of Correctness is Valid
P3: Proof Of Correctness is Valid
P4: Proof Of Correctness is Valid
P5: Proof Of Correctness is Valid
Bézout coefficients: (-15470, 489473)
y_e:  55622280246218557295863668194616821776243953991412012587806310279512622926217
H(M): 55622280246218557295863668194616821776243953991412012587806310279512622926217
SUCCESS!
<<completed>>
```

:warning: research grade implementation.

## Contact: munawar3008@gmail.com, munawar.hasan@nist.gov
