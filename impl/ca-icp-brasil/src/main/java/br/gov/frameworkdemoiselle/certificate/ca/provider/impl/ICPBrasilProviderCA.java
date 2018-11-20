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
package br.gov.frameworkdemoiselle.certificate.ca.provider.impl;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import br.gov.frameworkdemoiselle.certificate.ca.provider.ProviderCA;

/**
 * @deprecated replaced by Demoiselle SIGNER
 * @see <a href="https://github.com/demoiselle/signer/">https://github.com/demoiselle/signer</a>
 * 
 */
@Deprecated
public class ICPBrasilProviderCA implements ProviderCA {

	@Override
	public Collection<X509Certificate> getCAs() {
		KeyStore keyStore = this.getKeyStore();
		List<X509Certificate> result = new ArrayList<X509Certificate>();
		try {
			for (Enumeration<String> e = keyStore.aliases(); e.hasMoreElements();) {
				String alias = e.nextElement();
				X509Certificate root = (X509Certificate) keyStore.getCertificate(alias);
				result.add(root);
			}
		} catch (Throwable error) {
			throw new ICPBrasilProviderCAException("Error on load certificates from default keystore", error);
		}
		return result;
	}

	/**
	 * Pega o keystore interno do componente Tipo: JKS
	 */
	private KeyStore getKeyStore() {
		KeyStore keyStore = null;
		try {
			InputStream is = ICPBrasilProviderCA.class.getClassLoader().getResourceAsStream("icpbrasil.jks");
			keyStore = KeyStore.getInstance("JKS");
			keyStore.load(is, "changeit".toCharArray());
		} catch (Throwable error) {
			throw new ICPBrasilProviderCAException("KeyStore default not loaded.", error);
		}
		return keyStore;
	}

	@Override
	public String getName() {
		return "ICP Brasil Provider (Componente)";
	}
}
