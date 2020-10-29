# cryptography-server
# RSA Algorithms
one of the first practical public-key cryptosystems and is widely used for secure data transmission. In RSA, this asymmetry is based on the practical difficulty of factoring the product of two large prime numbers, the factoring problem.

# Introduction
This program aims to provide the means to encrypt and decrypt and sign and verify data with RSA using java in Spring Boot Framework and API.

# Information
Server port = 8081.
We generate RSA keys of 2048 bits as these are of good strength today.

# How to Use
First step is to generate the RSA key. 
  * URL: http://localhost:8081/api/generate

Secand step is encrypt the given data using the given key.
  * URL: http://localhost:8081/api/encrypt/{KeyId}?data={data}

third step is decrypt the given data using the given key.
  * URL:http://localhost:8081/api/decrypt/{keyId}?data={data}

Also could sign the given data using the given key 
  * URL:http://localhost:8081/api/sign/{keyId}?data={data}

Finally could Verify the given signature and data using the given key
  * URL:http://localhost:8081/api/verify/{keyId}?data={data}& signature={signature}
