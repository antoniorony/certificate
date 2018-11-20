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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Properties;

import br.gov.frameworkdemoiselle.certificate.CertificateValidatorException;
import br.gov.frameworkdemoiselle.certificate.util.RepositoryUtil;

/**
 * Representa o aplicativo de gerenciamneot dos arquivos de CRL. Recomenda-ser
 * criar um serviço que periodimente chame este aplicação para atulização das
 * CRL cadastrada no arquivo de índice.
 * @deprecated replaced by Demoiselle SIGNER
 * @see <a href="https://github.com/demoiselle/signer">https://github.com/demoiselle/signer</a>
 * 
 */
@Deprecated
public class RepositoryService {

    private static final String UPDATE = "update-crl-list";
    private static final String ADD = "add-crl";
    private static String rt = "";

    /**
     * Método principal
     *
     * @param args
     */
    public static void main(String[] args) {
        if (args == null || args.length < 2) {
            println(" Error: number of invalid arguments.\n " + "Use: java -jar demoiselle-certificate-core.jar [operation] <[url]> <[fileindex]>\n " + "Sample for update: java -jar security-certificate.jar " + UPDATE + " /tmp/crls/crl_index.txt\n " + "Sample for add url of crl: java -jar security-certificate.jar " + ADD
                    + " http://www.domain.org/file.clr /tmp/crls/crl_index.txt");
        } else {

            String op = args[0];
            if (op.equalsIgnoreCase(ADD)) {

                String url = args[1];
                String file_index = args[2];
                File file = new File(file_index);
                Configuration.getInstance().setCrlIndex(file.getName());
                Configuration.getInstance().setCrlPath(file.getParent());
                OffLineCRLRepository rp = new OffLineCRLRepository();
                rp.addFileIndex(url);
                update(url);

            } else if (op.equalsIgnoreCase(UPDATE)) {

                String file_index = args[1];
                File fileIndex = new File(file_index);
                Configuration.getInstance().setCrlIndex(file_index);

                if (!fileIndex.exists()) {
                    println("Index file [" + file_index + "] not found");

                } else {
                    Properties prop = new Properties();

                    try {
                        prop.load(new FileInputStream(fileIndex));
                    } catch (Exception e) {
                        throw new CertificateValidatorException("Error on load index file " + fileIndex, e);
                    }

                    Enumeration<Object> keys = prop.keys();
                    while (keys.hasMoreElements()) {
                        Object key = keys.nextElement();
                        String url = (String) prop.get(key);
                        update(url);
                    }

                    try {
                        prop.store(new FileOutputStream(fileIndex), null);
                    } catch (Exception e) {
                        throw new CertificateValidatorException("Error on load index file " + fileIndex, e);
                    }
                }

            } else {
                println("Invalid operation [" + op + "]");
            }
        }

    }

    private static void update(String url) {
        try {
            Configuration config = Configuration.getInstance();
            File fileCLR = new File(config.getCrlPath(), RepositoryUtil.urlToMD5(url));
            print(" Downloading [" + url + "]...");
            RepositoryUtil.saveURL(url, fileCLR);
            println("...[Ok]");
        } catch (CertificateValidatorException e) {
            println("...[Fail]");
            println("\tCause: " + e.getMessage());
        }
    }

    public static String getReturn() {
        return rt;
    }

    private static void println(String msg) {
        rt = msg;
        System.out.println(msg);
    }

    private static void print(String msg) {
        rt = msg;
        System.out.print(msg);
    }
}
