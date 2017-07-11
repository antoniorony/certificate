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
package br.gov.frameworkdemoiselle.certificate.ca.provider.hom;

import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.gov.frameworkdemoiselle.certificate.ca.provider.ProviderCA;

public class HomologacaoProviderCA implements ProviderCA {

    public Collection<X509Certificate> getCAs() {
        List<X509Certificate> result = new ArrayList<X509Certificate>();
        try {
            InputStream raizDeHomologacaoSERPRO = HomologacaoProviderCA.class.getClassLoader().getResourceAsStream("trustedca/RaizdeHomologacaoSERPRO.cer");
            InputStream ACSERPROACFv3Homologacao = HomologacaoProviderCA.class.getClassLoader().getResourceAsStream("trustedca/ACSERPROACFv3Homologacao.cer");
            InputStream ACSERPRORFBv3Homologacao = HomologacaoProviderCA.class.getClassLoader().getResourceAsStream("trustedca/ACSERPRORFBv3Homologacao.cer");
            InputStream intermediariaHOMv2 = HomologacaoProviderCA.class.getClassLoader().getResourceAsStream("trustedca/IntermediariaHOMv2.cer");
            InputStream ACSERPROACFv4Homologacao = HomologacaoProviderCA.class.getClassLoader().getResourceAsStream("trustedca/serproACFv4Homolog.cer");
            result.add((X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(raizDeHomologacaoSERPRO));
            result.add((X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(ACSERPROACFv3Homologacao));
            result.add((X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(ACSERPRORFBv3Homologacao));
            result.add((X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(intermediariaHOMv2));
            result.add((X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(ACSERPROACFv4Homologacao));
            
            InputStream raizDeHomologacaoSERPROV5 = HomologacaoProviderCA.class.getClassLoader().getResourceAsStream("trustedca/RaizdeHomologacaoSERPROV5.cer");
            InputStream intermediariaHOMv6 = HomologacaoProviderCA.class.getClassLoader().getResourceAsStream("trustedca/IntermediariaHOMv6.cer");
            InputStream ACSERPROACFv5Homologacao = HomologacaoProviderCA.class.getClassLoader().getResourceAsStream("trustedca/serproACFv5Homolog.cer");
            result.add((X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(raizDeHomologacaoSERPROV5));
            result.add((X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(intermediariaHOMv6));
            result.add((X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(ACSERPROACFv5Homologacao));
        } catch (Throwable error) {
            error.printStackTrace();
            return null;
        } finally {
            return result;
        }
    }

    public String getName() {
         return "Homologacao SERPRO Provider";
    }
}
