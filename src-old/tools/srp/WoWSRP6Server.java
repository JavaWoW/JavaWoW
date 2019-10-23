package tools.srp;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.agreement.srp.SRP6Server;
import org.bouncycastle.crypto.params.SRP6GroupParameters;

public final class WoWSRP6Server extends SRP6Server {
	private byte[] I;
	private byte[] s;

	private WoWSRP6Server() {
	}

	public static final WoWSRP6Server init(SRP6GroupParameters params, BigInteger v, byte[] I, byte[] s, Digest digest, SecureRandom random) {
		WoWSRP6Server srp = new WoWSRP6Server();
		srp.initInternal(params, v, I, s, digest, random);
		return srp;
	}

	private final void initInternal(SRP6GroupParameters params, BigInteger v, byte[] I, byte[] s, Digest digest, SecureRandom random) {
		this.I = I.clone();
		this.s = s.clone();
		super.init(params.getN(), params.getG(), v, digest, random);
	}

	@Override
	public final void init(SRP6GroupParameters params, BigInteger v, Digest digest, SecureRandom random) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void init(BigInteger N, BigInteger g, BigInteger v, Digest digest, SecureRandom random) {
		throw new UnsupportedOperationException();
	}

	/**
	* Generates the server's credentials that are to be sent to the client.
	* @return The server's public value to the client
	*/
	@Override
	public final BigInteger generateServerCredentials() {
		BigInteger k = BigInteger.valueOf(3); // k = 3 for legacy SRP-6
		this.b = selectPrivateValue();
		this.B = k.multiply(v).mod(N).add(g.modPow(b, N)).mod(N);
		return B;
	}

	/**
	* Processes the client's credentials. If valid the shared secret is generated and returned.
	* @param clientA The client's credentials
	* @return A shared secret BigInteger
	* @throws CryptoException If client's credentials are invalid
	*/
	@Override
	public final BigInteger calculateSecret(BigInteger clientA) throws CryptoException {
		this.A = WoWSRP6Util.validatePublicValue(N, clientA);
		this.u = WoWSRP6Util.calculateU(digest, N, A, B);
		this.S = calculateS();
		return S;
	}

	private final BigInteger calculateS() {
		// XXX Might need to modify this method to have the code below
		BigInteger S = v.modPow(u, N).multiply(A).mod(N).modPow(b, N);
		/*// Take S and convert it into a little-endian byte array
		byte[] S_le = BitTools.toLEByteArray(S, 32);
		byte[] S_even_bytes = new byte[16];
		byte[] S_odd_bytes = new byte[16];
		// Read in the even-indexed bytes
		for (int i = 0, c = 0; i < 32; i += 2) {
			S_even_bytes[c++] = S_le[i];
		}
		for (int i = 1, c = 0; i < 32; i += 2) {
			S_odd_bytes[c++] = S_le[i];
		}
		digest.update(S_even_bytes, 0, S_even_bytes.length);
		byte[] S_even_digested_bytes = new byte[digest.getDigestSize()];
		digest.doFinal(S_even_digested_bytes, 0);
		digest.update(S_odd_bytes, 0, S_odd_bytes.length);
		byte[] S_odd_digested_bytes = new byte[digest.getDigestSize()];
		digest.doFinal(S_odd_digested_bytes, 0);
		byte[] hashed = new byte[digest.getDigestSize() * 2];
		// copy the even-indexed bytes in
		for (int i = 0, c = 0; i < 40; i += 2) {
			hashed[i] = S_even_digested_bytes[c++];
		}
		// copy the odd-indexed bytes in
		for (int i = 1, c = 0; i < 40; i += 2) {
			hashed[i] = S_odd_digested_bytes[c++];
		}
		return new BigInteger(1, hashed);*/
		return S;
	}

	/**
	* Authenticates the received client evidence message M1 and saves it only if correct.
	* To be called after calculating the secret S.
	* @param M1: the client side generated evidence message
	* @return A boolean indicating if the client message M1 was the expected one.
	* @throws CryptoException
	*/
	@Override
	public final boolean verifyClientEvidenceMessage(BigInteger clientM1) throws CryptoException {
		// Verify pre-requirements
		if (this.A == null || this.B == null || this.S == null) {
			throw new CryptoException("Impossible to compute and verify M1: some data are missing from the previous operations (A,B,S)");
		}
		// Compute the own client evidence message 'M1'
		BigInteger computedM1 = WoWSRP6Util.calculateM1(digest, N, g, I, s, A, B, S);
		if (computedM1.equals(clientM1)) {
			this.M1 = clientM1;
			return true;
		}
		return false;
	}

	/**
	* Computes the server evidence message M2 using the previously verified values.
	* To be called after successfully verifying the client evidence message M1.
	* @return M2: the server side generated evidence message
	* @throws CryptoException
	*/
	public final BigInteger calculateServerEvidenceMessage() throws CryptoException {
		// Verify pre-requirements
		if (this.A == null || this.M1 == null || this.S == null) {
			throw new CryptoException("Impossible to compute M2: some data are missing from the previous operations (A,M1,S)");
		}
		// Compute the server evidence message 'M2'
		this.M2 = WoWSRP6Util.calculateM2(digest, N, A, M1, S);
		return M2;
	}
}