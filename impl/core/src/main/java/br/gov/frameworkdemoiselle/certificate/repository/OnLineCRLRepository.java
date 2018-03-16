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
package br.gov.frameworkdemoiselle.certificate.repository;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.gov.frameworkdemoiselle.certificate.extension.BasicCertificate;
import br.gov.frameworkdemoiselle.certificate.extension.ICPBR_CRL;

/**
 * Representa um repositório online. Neste caso não ha necessidade de um serviço
 * para atualização das CRL. O Repositório deve primeiramente buscar a arquivo
 * no file system, caso o mesmo não se encontre ou ja esteja expirado ele obterá
 * a CRL a partir de sua URL.
 */
public class OnLineCRLRepository implements CRLRepository {

    private static final Logger LOGGER = Logger.getLogger(OnLineCRLRepository.class.getName());

    @Override
    public Collection<ICPBR_CRL> getX509CRL(X509Certificate certificate) {

        Collection<ICPBR_CRL> list = new ArrayList<ICPBR_CRL>();
        try {
            BasicCertificate cert = new BasicCertificate(certificate);
            List<String> ListaURLCRL = cert.getCRLDistributionPoint();

            if (ListaURLCRL == null || ListaURLCRL.isEmpty()) {
                throw new CRLRepositoryException("Could not get a valid CRL from Certificate");
            }

            ICPBR_CRL crl = null;
            for (String URLCRL : ListaURLCRL) {
                // Achou uma CRL válida
                crl = getICPBR_CRL(URLCRL);
                if (crl != null) {
                    list.add(crl);
                    LOGGER.log(Level.INFO, "A valid Crl was found. It''s not necessary to continue. CRL=[{0}]", URLCRL);
                    break;
                }
            }
            if (crl == null){
            	throw new CRLRepositoryException("Não foi possível verficar se o certificado está revogado, verifique se há conexão de internet" );
            }

        } catch (IOException e) {
            throw new CRLRepositoryException("Could not get the CRL List from Certificate " + e, e);
        }
        return list;
    }

    private ICPBR_CRL getICPBR_CRL(String uRLCRL) {
        try {
            URL url = new URL(uRLCRL);
            URLConnection conexao = url.openConnection();
            conexao.setConnectTimeout(5000);
            conexao.setReadTimeout(5000);
            DataInputStream inStream = new DataInputStream(conexao.getInputStream());
            ICPBR_CRL icpbr_crl = new ICPBR_CRL(inStream);
            inStream.close();
            return icpbr_crl;

        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE, "URL da LCR malformatada", e.getMessage());
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Nao foi possivel conectar a {0}", e.getMessage());
        } catch (CRLException e) {
            LOGGER.log(Level.SEVERE, "LCR inválida", e.getMessage());
        } catch (CertificateException e) {
            LOGGER.log(Level.SEVERE, "Certificado da LCR inválido", e.getMessage());
        }
        return null;
    }
}
