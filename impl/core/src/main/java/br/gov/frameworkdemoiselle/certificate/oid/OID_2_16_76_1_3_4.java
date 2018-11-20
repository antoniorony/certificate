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
package br.gov.frameworkdemoiselle.certificate.oid;

import java.util.logging.Logger;

/**
 *
 * OID = 2.16.76.1.3.4 e conteúdo = nas primeiras 8 (oito) posições, a data de
 * nascimento do responsável pelo certificado, no formato ddmmaaaa; nas 11
 * (onze) posições subsequentes, o Cadastro de Pessoa Física (CPF) do
 * responsável; nas 11 (onze) posições subsequentes, o número de Identificação
 * Social – NIS (PIS, PASEP ou CI); nas 15 (quinze) posições subsequentes, o
 * número do RG do responsável; nas 10 (dez) posições subsequentes, as siglas do
 * órgão expedidor do RG e respectiva UF.
 *
 * @author Humberto Pacheco - SERVICO FEDERAL DE PROCESSAMENTO DE DADOS
 * @deprecated replaced by Demoiselle SIGNER
 * @see <a href="https://github.com/demoiselle/signer">https://github.com/demoiselle/signer</a>
 * 
 */
@Deprecated
public class OID_2_16_76_1_3_4 extends OIDGeneric {

    private static final Logger LOGGER = Logger.getLogger(OID_2_16_76_1_3_4.class.getName());

    public static final String OID = "2.16.76.1.3.4";

    protected static final Object CAMPOS[] = {"DATA_NASCIMENTO", 8, "CPF", 11, "NIS", 11, "RG", 15, "ORGAO_UF_EXPEDIDOR", 10};

    public OID_2_16_76_1_3_4() {
    }

    @Override
    public void initialize() {
        super.initialize(CAMPOS);
    }

    /**
     *
     * @return a data de nascimento do titular
     */
    public String getDataNascimento() {
        return properties.get("DATA_NASCIMENTO");
    }

    /**
     *
     * Retorna o Cadastro de Pessoa Física (CPF) do responsável pelo e-CNPJ
     *
     * @return O número do Cadastro de Pessoa Fisica (CPF)
     */
    public String getCPF() {
        return properties.get("CPF");
    }

    /**
     *
     * @return o numero de Identificacao Social - NIS (PIS, PASEP ou CI)
     */
    public String getNIS() {
        return properties.get("NIS");
    }

    /**
     *
     * @return numero do Registro Geral - RG do titular
     */
    public String getRg() {
        return properties.get("RG");
    }

    /**
     *
     * @return as siglas do orgao expedidor do RG
     */
    public String getOrgaoExpedidorRg() {

        String s = properties.get("ORGAO_UF_EXPEDIDOR").trim();
        int len = s.trim().length();
        String ret = null;
        if (len > 0) {
            ret = s.substring(0, len - 2);
        }
        return ret;
    }

    /**
     *
     * @return a UF do orgao expedidor do RG
     */
    public String getUfExpedidorRg() {
        String s = properties.get("ORGAO_UF_EXPEDIDOR").trim();
        int len = s.trim().length();
        String ret = null;
        if (len > 0) {
            ret = s.substring(len - 2, len);
        }
        return ret;

    }

}
