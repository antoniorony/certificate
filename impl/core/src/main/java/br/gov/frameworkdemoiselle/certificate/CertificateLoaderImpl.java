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

package br.gov.frameworkdemoiselle.certificate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import br.gov.frameworkdemoiselle.certificate.keystore.loader.KeyStoreLoader;
import br.gov.frameworkdemoiselle.certificate.keystore.loader.factory.KeyStoreLoaderFactory;

/**
 * @deprecated replaced by Demoiselle SIGNER
 * @see <a href="https://github.com/demoiselle/signer/">https://github.com/demoiselle/signer</a>
 * 
 */
@Deprecated
public class CertificateLoaderImpl implements CertificateLoader {

	private KeyStore keyStore;

	@Override
	public KeyStore getKeyStore() {
		return keyStore;
	}

	@Override
	public void setKeyStore(KeyStore keyStore) {
		this.keyStore = keyStore;
	}

	@Override
	public X509Certificate load(File file) {
		try {
			FileInputStream fileInput = new FileInputStream(file);
			return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(fileInput);
		} catch (FileNotFoundException e) {
			throw new br.gov.frameworkdemoiselle.certificate.CertificateException("FileNotFoundException", e);
		} catch (CertificateException e) {
			throw new br.gov.frameworkdemoiselle.certificate.CertificateException("CertificateException", e);
		}
	}

	@Override
	public X509Certificate loadFromToken() {
		return loadFromToken(null);
	}

	@Override
	public X509Certificate loadFromToken(String pinNumber) {
		if (this.keyStore == null) {
			KeyStoreLoader keyStoreLoader = KeyStoreLoaderFactory.factoryKeyStoreLoader();
			this.keyStore = keyStoreLoader.getKeyStore();
		}
		String alias;
		try {
			alias = this.keyStore.aliases().nextElement();
			return (X509Certificate) this.keyStore.getCertificateChain(alias)[0];
		} catch (KeyStoreException e) {
			throw new br.gov.frameworkdemoiselle.certificate.CertificateException("", e);
		}

	}

	@Override
	public X509Certificate loadFromToken(String pinNumber, String alias) {
		if (this.keyStore == null) {
			KeyStoreLoader keyStoreLoader = KeyStoreLoaderFactory.factoryKeyStoreLoader();
			this.keyStore = keyStoreLoader.getKeyStore();
		}
		try {
			return (X509Certificate) this.keyStore.getCertificateChain(alias)[0];
		} catch (KeyStoreException e) {
			throw new br.gov.frameworkdemoiselle.certificate.CertificateException("", e);
		}
	}

}
