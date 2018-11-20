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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownServiceException;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.DatatypeConverter;

import br.gov.frameworkdemoiselle.certificate.ca.provider.ProviderCA;

/**
 * @deprecated replaced by Demoiselle SIGNER
 * @see <a href="https://github.com/demoiselle/signer/">https://github.com/demoiselle/signer</a>
 * 
 */
@Deprecated

public class ICPBrasilOnLineSerproProviderCA implements ProviderCA {

	private static final String STRING_URL_ZIP = "http://repositorio.serpro.gov.br/icp-brasil/ACcompactado.zip";
	private static final String STRING_URL_HASH = "http://repositorio.serpro.gov.br/icp-brasil/hashsha512.txt";
	private static final int TIMEOUT_CONNECTION = 3000;
	private static final int TIMEOUT_READ = 5000;

	private static final Logger LOGGER = Logger.getLogger(ICPBrasilOnLineSerproProviderCA.class.getName());

	public String getURLZIP() {
		return ICPBrasilOnLineSerproProviderCA.STRING_URL_ZIP;
	}

	public String getURLHash() {
		return ICPBrasilOnLineSerproProviderCA.STRING_URL_HASH;
	}

	@Override
	public Collection<X509Certificate> getCAs() {

		Collection<X509Certificate> result = null;
		boolean useCache = false;

		try {

			// Faz o hash do checksum do arquivo e não usa o local de propósito,
			// pois o arquivo pode ter sido corrompido e neste caso o check vai
			// dar errado e baixar novamente
			String pathZip = ICPBrasilUserHomeProviderCA.FULL_PATH_ZIP;
			File filePathZip = new File(pathZip);
			if (filePathZip.exists()) {

				// Baixa o hash do endereço online
				InputStream inputStreamHash = getInputStreamFromURL(getURLHash());

				// Convert o input stream em string
				Scanner scannerOnlineHash = new Scanner(inputStreamHash);
				scannerOnlineHash.useDelimiter("\\A");
				String onlineHash = scannerOnlineHash.hasNext() ? scannerOnlineHash.next() : "";
				scannerOnlineHash.close();

				if (!onlineHash.equals("")) {

					// Gera o hash do arquivo local
					String localZipHash = DatatypeConverter.printHexBinary(checksum(filePathZip));
					
					// Pega SOMENTE o hash sem o nome do arquivo
					String onlineHashWithouFilename = onlineHash.replace(ICPBrasilUserHomeProviderCA.FILENAME_ZIP, "")
							.replaceAll(" ", "").replaceAll("\n", "");

					if (onlineHashWithouFilename.equalsIgnoreCase(localZipHash)) {
						useCache = true;
					} else {
						useCache = false;
					}

				} else {
					LOGGER.log(Level.WARNING, "Ocorreu um erro ao obter o hash online, pois está vazio.");
				}
			}

			// Se não é para pegar do cache os certificados ele baixa o novo e
			// salva localmente
			if (!useCache) {
				// Baixa um novo arquivo
				LOGGER.log(Level.INFO, "Recuperando REMOTAMENTE as cadeias da ICP-Brasil [" + getURLZIP() + "].");
				InputStream inputStreamZip = getInputStreamFromURL(getURLZIP());
				
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				int nRead;
				byte[] data = new byte[16384];
				while ((nRead = inputStreamZip.read(data, 0, data.length)) != -1)
					buffer.write(data, 0, nRead);
				buffer.flush();
				byte[] content = buffer.toByteArray();
				
				FileOutputStream out = new FileOutputStream(filePathZip);
				out.write(content);
				out.close();
				
				inputStreamZip.close();

				LOGGER.log(Level.INFO, "Cadeias da ICP-Brasil recupedadas com sucesso.");	
			}
			
			// Pega os certificados locais
			InputStream inputStreamZipReturn = new FileInputStream(pathZip.toString());
			result = getFromZip(inputStreamZipReturn);
			inputStreamZipReturn.close();

			LOGGER.log(Level.INFO, "Recuperou [" + result.size() + "] certificados do arquivo que foi baixado.");

		} catch (IOException e) {			
			LOGGER.log(Level.WARNING, "Erro ao tentar recuperar a cadeia.", e);			
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erro inesperado ao tentar recuperar a cadeia.", e);
		}

		if (result != null) {
			LOGGER.log(Level.INFO, "O Provider " + getName() + " possui [" + result.size() + "] certificados.");
		} else {
			LOGGER.log(Level.INFO, "O Provider " + getName() + " NÃO possui certificados.");
		}

		return result;
	}

	public byte[] checksum(File input) throws IOException {
		InputStream in = null;
		try {
			in = new FileInputStream(input);

			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			byte[] block = new byte[4096];
			int length;
			while ((length = in.read(block)) > 0) {
				digest.update(block, 0, length);
			}
			return digest.digest();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				in.close();

		}
		return null;
	}

	public Collection<X509Certificate> getOnline(InputStream zip) {
		Collection<X509Certificate> result = new HashSet<X509Certificate>();
		long timeBefore = 0;
		long timeAfter = 0;
		try {
			timeBefore = System.currentTimeMillis();
			result = this.getFromZip(zip);
			timeAfter = System.currentTimeMillis();
		} catch (Throwable error) {
			timeAfter = System.currentTimeMillis();
			LOGGER.log(Level.WARNING, "ERRO. [" + error.getMessage() + "].");
		} finally {
			LOGGER.log(Level.INFO, "Levamos " + (timeAfter - timeBefore) + "ms para recuperar as cadeias.");
		}

		return result;
	}

	public Collection<X509Certificate> getFromZip(InputStream zip) throws RuntimeException {
		Collection<X509Certificate> result = new HashSet<X509Certificate>();
		InputStream in = new BufferedInputStream(zip);
		ZipInputStream zin = new ZipInputStream(in);
		ZipEntry arquivoInterno = null;
		try {
			while ((arquivoInterno = zin.getNextEntry()) != null) {
				if (!arquivoInterno.isDirectory()) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					byte[] b = new byte[512];
					int len = 0;
					while ((len = zin.read(b)) != -1)
						out.write(b, 0, len);
					ByteArrayInputStream is = new ByteArrayInputStream(out.toByteArray());
					out.close();
					X509Certificate certificate = (X509Certificate) CertificateFactory.getInstance("X509")
							.generateCertificate(is);
					is.close();
					result.add(certificate);
				}
			}
		} catch (CertificateException error) {
			throw new RuntimeException("Certificado inválido", error);
		} catch (IOException error) {
			throw new RuntimeException("Erro ao tentar abrir o stream", error);
		}
		return result;
	}

	public InputStream getInputStreamFromURL(String stringURL) throws RuntimeException {
		try {
			URL url = new URL(stringURL);
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(TIMEOUT_CONNECTION);
			connection.setReadTimeout(TIMEOUT_READ);
			return connection.getInputStream();
		} catch (MalformedURLException error) {
			throw new RuntimeException("URL mal formada", error);
		} catch (UnknownServiceException error) {
			throw new RuntimeException("Serviço da URL desconhecido", error);
		} catch (IOException error) {
			throw new RuntimeException("Algum erro de I/O ocorreu", error);
		}
	}

	@Override
	public String getName() {
		return "ICP Brasil ONLINE SERPRO Provider (" + getURLZIP() + ")";
	}

}