/*
 * Demoiselle Framework
 * Copyright (C) 2010 SERPRO
 * ----------------------------------------------------------------------------
 * This file is part of Demoiselle Framework.
 *
 * Demoiselle Framework is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License version 3
 * along with this program; if not,  see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301, USA.
 * ----------------------------------------------------------------------------
 * Este arquivo é parte do Framework Demoiselle.
 *
 * O Framework Demoiselle é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da GNU LGPL versão 3 como publicada pela Fundação
 * do Software Livre (FSF).
 *
 * Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA
 * GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou
 * APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU/LGPL em português
 * para maiores detalhes.
 *
 * Você deve ter recebido uma cópia da GNU LGPL versão 3, sob o título
 * "LICENCA.txt", junto com esse programa. Se não, acesse <http://www.gnu.org/licenses/>
 * ou escreva para a Fundação do Software Livre (FSF) Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02111-1301, USA.
 */
package br.gov.frameworkdemoiselle.certificate.ca.manager;

import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.gov.frameworkdemoiselle.certificate.ca.provider.ProviderCA;
import br.gov.frameworkdemoiselle.certificate.ca.provider.ProviderCAFactory;
import br.gov.frameworkdemoiselle.certificate.ca.provider.ProviderSignaturePolicyRootCA;
import br.gov.frameworkdemoiselle.certificate.ca.provider.ProviderSignaturePolicyRootCAFactory;

public class CAManager {

	private static final CAManager instance = new CAManager();
	private static final Logger LOGGER = Logger.getLogger(CAManager.class.getName());

	private CAManager() {
	}

	public static CAManager getInstance() {
		return CAManager.instance;
	}

	public Collection<X509Certificate> getSignaturePolicyRootCAs(String policyOID) {
		Collection<ProviderSignaturePolicyRootCA> providers = ProviderSignaturePolicyRootCAFactory.getInstance()
				.factory(policyOID);
		Collection<X509Certificate> result = new HashSet<X509Certificate>();
		for (ProviderSignaturePolicyRootCA provider : providers) {
			try {
				for (X509Certificate caCertificate : provider.getCAs()){
					LOGGER.info("Adicionando AC"+ caCertificate.getSubjectDN().getName());
					result.add(caCertificate);
				}
				//result.addAll(provider.getCAs());
			} catch (Throwable error) {
				// TODO: Nao foi possivel resgatar as raizes confiaveis
				// de uma determinada politica
			}
		}
		return result;
	}

	public boolean validateRootCAs(Collection<X509Certificate> cas, X509Certificate certificate) {
		boolean valid = false;
		for (X509Certificate ca : cas) {
			try {
				this.validateRootCA(ca, certificate);
				valid = true;
				break;
			} catch (CAManagerException error) {
				LOGGER.info("Erro do ValidateRooCAs: "+error.getMessage());
				continue;
			}
		}
		if (!valid) {
			throw new CAManagerException(
					"Nenhuma autoridade informada faz parte da cadeia de certificados do certificado informado");
		}
		return true;
	}

	public boolean validateRootCA(X509Certificate ca, X509Certificate certificate) {
		boolean retorno =false;
		if (ca == null) {
			throw new CAManagerException("Certificado da autoridade raiz não informado");
		}
		if (!this.isRootCA(ca)) {
			throw new CAManagerException("Certificado da autoridade não é raiz");
		}
		Collection<X509Certificate> acs = this.getCertificateChain(certificate);
		if (acs == null || acs.size() <= 0) {
			throw new CAManagerException("Não foi possível resgatar a cadeia de autoridades do certificado informado");
		}
		X509Certificate rootCA = null;
		for (X509Certificate x509 : acs) {
			if (this.isRootCA(x509)) {
				rootCA = x509;
				retorno =true;
				break;
			}
		}
		if (rootCA == null) {
			throw new CAManagerException(
					"Não foi possível achar um certificado raiz na cadeia do certificado informado");
		}

		if (!this.isCAofCertificate(rootCA, ca)) {
			retorno = false;
			throw new CAManagerException(
					"A autoridade raiz não faz parte da cadeia de certificados do certificado informado");
		}
		return retorno;
	}

	public boolean isRootCA(X509Certificate ca) {
		if (ca == null) {
			return false;
		}
		return this.isCAofCertificate(ca, ca);
	}

	public boolean isCAofCertificate(X509Certificate ca, X509Certificate certificate) {
		try {
			certificate.verify(ca.getPublicKey());
			return true;
		} catch (SignatureException error) {
			return false;
		} catch (InvalidKeyException error) {
			return false;
		} catch (CertificateException error) {
			throw new CAManagerException("Algum erro ocorreu com o certificado informado", error);
		} catch (NoSuchAlgorithmException error) {
			throw new CAManagerException("Não há o algoritmo necessário", error);
		} catch (NoSuchProviderException error) {
			throw new CAManagerException("Provider inválido", error);
		}
	}

	public Certificate[] getCertificateChainArray(X509Certificate certificate) {
		Certificate[] result = null;
		LinkedList<X509Certificate> chain = (LinkedList<X509Certificate>) this.getCertificateChain(certificate);
		if (chain == null || chain.size() <= 0) {
			return result;
		}
		result = new Certificate[chain.size()];
		for (int i = 0; i < chain.size(); i++) {
			result[i] = chain.get(i);
		}
		return result;
	}

	/**
	 * Get ALL certificate chains previously added in
	 * 
	 * @param certificate
	 * @return list of certificates
	 */
	public Collection<X509Certificate> getCertificateChain(X509Certificate certificate) {

		Collection<X509Certificate> result = new LinkedList<X509Certificate>();
		result.add(certificate);
		if (this.isRootCA(certificate)) {
			return result;
		}

		Collection<ProviderCA> providers = ProviderCAFactory.getInstance().factory();

		for (ProviderCA provider : providers) {
			try {

				// Get ALL CAs of ONE provider
				Collection<X509Certificate> acs = provider.getCAs();

				// Variable to control if go to next Provider is necessery
				boolean ok = false;

				// Iterate this provider to create a Cert Chain
				for (X509Certificate ac : acs) {
					if (this.isCAofCertificate(ac, certificate)) {
						result.add(ac);
						X509Certificate acFromAc = this.getCAFromCertificate(acs, ac);
						while (acFromAc != null) {
							// If the chain was created SET OK
							result.add(acFromAc);
							// If Certificate is ROOT end while
							if (this.isRootCA(acFromAc)) {
								ok = true;
								break;
							} else {
								acFromAc = this.getCAFromCertificate(acs, acFromAc);
							}
						}
					}
					if (ok == true) {
						break;
					}
				}

				LOGGER.log(Level.INFO, ">>> Foram encontrados [" + result.size() + "] níveis na cadeia do provider ["
						+ provider.getName() + "].");

				// If chain is created BREAK! Doesn't go to next Provider
				if (ok) {
					break;
				} else {
					LOGGER.info("Não foi possivel montar a cadeia com o provider: " + provider.getName());
				}

			} catch (Throwable error) {
				// TODO: Nao foi possivel resgatar as CAs de um determinado
				// provedor
			}
		}

		return result;
	}

	private X509Certificate getCAFromCertificate(Collection<X509Certificate> certificates,
			X509Certificate certificate) {
		if (this.isRootCA(certificate)) {
			return null;
		}
		if (certificates == null || certificates.isEmpty()) {
			return null;
		}
		for (X509Certificate ca : certificates) {
			if (this.isCAofCertificate(ca, certificate)) {
				return ca;
			}
		}
		return null;
	}

	public Certificate[] getCertificateChainArray(KeyStore keyStore, String privateKeyPass, String certificateAlias) {
		Certificate[] certificateChain = null;
		try {
			keyStore.getKey(certificateAlias, privateKeyPass.toCharArray());
			certificateChain = keyStore.getCertificateChain(certificateAlias);
			if (certificateChain == null) {
				throw new CAManagerException("Não há caminho de certificação para o alias informado");
			}
		} catch (KeyStoreException error) {
			throw new CAManagerException("O provedor não suporta este tipo de keystore", error);
		} catch (UnrecoverableKeyException error) {
			throw new CAManagerException("Impossível recuperar a chave privada do keystore", error);
		} catch (NoSuchAlgorithmException error) {
			throw new CAManagerException("Não há o algoritmo necessário", error);
		}
		return certificateChain;
	}

	public Collection<X509Certificate> getCertificateChain(KeyStore keyStore, String privateKeyPass,
			String certificateAlias) {
		Collection<X509Certificate> result = null;
		Certificate[] certificateChain = this.getCertificateChainArray(keyStore, privateKeyPass, certificateAlias);
		if (certificateChain != null) {
			result = new LinkedList<X509Certificate>();
			for (Certificate certificate : certificateChain) {
				result.add((X509Certificate) certificate);
			}
		} else {
			throw new CAManagerException("Não há caminho de certificação para o alias informado");
		}
		return result;
	}
}