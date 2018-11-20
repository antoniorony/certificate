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
 * Class OID 1.3.6.1.4.1.311.20.2.3 <br>
 *
 * <br>
 * UPN attribute: Used by micro$oft/window$ for logon with SmartCard, <br>
 * Some Certificate Autority has included this attribute in their certificates.
 * Is NOT a ICP-BRASIL pattern attribute <b>*</b> <br>
 *
 * <br>
 * Atributo conhecido como UPN: Utilizado pela micro$oft para logon com
 * SmartCard, <br>
 * Presente em alguns cartoes de determinadas autoridades. NAO eh padrao da
 * ICP-BRASIL <b>*</b> <br>
 *
 * @author CETEC/CTCTA
 *
 * @deprecated replaced by Demoiselle SIGNER
 * @see <a href="https://github.com/demoiselle/signer">https://github.com/demoiselle/signer</a>
 * 
 */
@Deprecated
public class OID_1_3_6_1_4_1_311_20_2_3 extends OIDGeneric {

    private static final Logger logger = Logger.getLogger(OID_1_3_6_1_4_1_311_20_2_3.class.getName());

    public static final String OID = "1.3.6.1.4.1.311.20.2.3";

    public OID_1_3_6_1_4_1_311_20_2_3() {
    }

    @Override
    public void initialize() {
    }

    /**
     *
     * @return UPN
     */
    public String getUPN() {
        return super.getData();
    }

}
