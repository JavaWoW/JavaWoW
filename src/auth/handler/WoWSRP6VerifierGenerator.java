package auth.handler;

import java.math.BigInteger;

import org.bouncycastle.crypto.agreement.srp.SRP6VerifierGenerator;

public final class WoWSRP6VerifierGenerator extends SRP6VerifierGenerator {
	/**
	* Creates a new SRP verifier
	* @param salt The salt to use, generally should be large and random
	* @param identity The user's identifying information (eg. username)
	* @param password The user's password
	* @return A new verifier for use in future SRP authentication
	*/
	@Override
	public final BigInteger generateVerifier(byte[] salt, byte[] identity, byte[] password) {
		BigInteger x = WoWSRP6Util.calculateX(digest, N, salt, identity, password);
		return g.modPow(x, N);
	}
}