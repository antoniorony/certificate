#/bin/bash

# modifica Manifest.MF dos arquivos .jar
echo "*** modificando os arquivos .jar ..."

# jar ufm jar-file manifest-addition.txt


# jar ufm assinadorweb-desktop-1.2.2.jar manifest-addition-certificate.txt
# jar ufm demoiselle-certificate-ca-icpbrasil-1.2.2.jar manifest-addition-certificate.txt
# jar ufm demoiselle-certificate-ca-icpbrasil-homologacao-1.2.2.jar manifest-addition-certificate.txt
# jar ufm demoiselle-certificate-core-1.2.2.jar manifest-addition-certificate.txt
# jar ufm demoiselle-certificate-criptography-1.2.2.jar manifest-addition-certificate.txt
# jar ufm demoiselle-certificate-desktop-1.2.2.jar manifest-addition-certificate.txt
# jar ufm demoiselle-certificate-signer-1.2.2.jar manifest-addition-certificate.txt
# jar ufm bcmail-jdk15-1.45.jar manifest-addition-certificate.txt
# jar ufm bcprov-jdk15-1.45.jar manifest-addition-certificate.txt
jar ufm log4j-1.2.16.jar manifest-addition-certificate.txt
jar ufm slf4j-log4j12-1.6.1.jar manifest-addition-certificate.txt
jar ufm slf4j-api-1.6.1.jar manifest-addition-certificate.txt

read -s -n 1 -p "Press any key to continue…"

echo "*** Fim!"





