package examples;

import curve_wrapper.*;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import zero_knowledge_proofs.*;
import zero_knowledge_proofs.CryptoData.*;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class OR_Fiat_Shamir_AADProverBasicECSchnorrORExample {

    public CryptoData[] prover(int n, int iReal) throws Exception {
        SecureRandom rand = new SecureRandom();

        // Setup curve
        ECPoint gUnwrapped = ECNamedCurveTable.getParameterSpec("secp256k1").getG();
        ECCurve cUnwrapped = gUnwrapped.getCurve();
        BigInteger order = cUnwrapped.getOrder();
        ECPointWrapper g = new BouncyCastlePoint(gUnwrapped);
        ECCurveWrapper c = new BouncyCastleCurve(cUnwrapped);

        // Generate secrets and y = g^x in parallel
        BigInteger[] x = new BigInteger[n];
        ECPointWrapper[] y = new ECPointWrapper[n];

        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            final int idx = i;
            tasks.add(pool.submit(() -> {
                x[idx] = ZKToolkit.random(order, rand);
                y[idx] = g.multiply(x[idx]);
                return null;
            }));
        }

        for (Future<Void> task : tasks) task.get();
        pool.shutdown();

        // Create ZK Protocol
        ZKPProtocol[] innerProtocols = new ZKPProtocol[n];
        Arrays.setAll(innerProtocols, i -> new ECSchnorrProver());
        ZKPProtocol proof = new ZeroKnowledgeOrProver(innerProtocols, order);

        // Public Inputs
        CryptoDataArray[] pub = new CryptoDataArray[n];
        for (int i = 0; i < n; i++) pub[i] = new CryptoDataArray(new CryptoData[]{new ECPointData(y[i])});
        CryptoData publicInputs = new CryptoDataArray(pub);

        // Secrets and simulation challenges
        BigInteger[] simChallenges = new BigInteger[n];
        CryptoData[] secretsTemp = new CryptoData[n + 1];
        for (int i = 0; i < n; i++) {
            if (i == iReal) {
                BigInteger r = ZKToolkit.random(order, rand);
                secretsTemp[i] = new CryptoDataArray(new BigInteger[]{r, x[i]});
                simChallenges[i] = null;
            } else {
                BigInteger fakeR = ZKToolkit.random(order, rand);
                secretsTemp[i] = new CryptoDataArray(new BigInteger[]{fakeR});
                simChallenges[i] = ZKToolkit.random(order, rand);
            }
        }
        secretsTemp[n] = new CryptoDataArray(simChallenges);
        CryptoData secrets = new CryptoDataArray(secretsTemp);

        // Environment (shared instance)
        CryptoDataArray commonEnv = new CryptoDataArray(new CryptoData[]{new ECCurveData(c, g)});
        CryptoData[] envTemp = new CryptoDataArray[n];
        Arrays.fill(envTemp, commonEnv);
        CryptoData env = new CryptoDataArray(envTemp);

        
        
        CryptoData[] shortArr = proof.proveFiatShamir(publicInputs, secrets, env); // returns {a, z}
        
        // Build a new array: {publicInput, a, z, environment}
        CryptoData[] combined = new CryptoData[shortArr.length + 2];
        System.arraycopy(shortArr, 0, combined, 1, shortArr.length);
        combined[0] = publicInputs;
        combined[shortArr.length + 1] = env;
        
        return combined;
    }
}