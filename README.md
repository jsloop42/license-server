# A License Server
v1.0.0

This is a license server. It can be used to generate a license from a text message and signing it with the private key. The license and the text message can then be verified using the public key. It uses RSA cryptography.

## Usage

### Generate RSA private key and public key

```sh
# Generate a 2048-bit RSA private key
$ openssl genrsa -out privatekey.pem 2048

# Convert private Key to PKCS#8 in DER format
$ openssl pkcs8 -topk8 -inform PEM -outform DER -in privatekey.pem -out privatekey.der -nocrypt

# Output public key in DER format
$ openssl rsa -in privatekey.pem -pubout -outform DER -out publickey.der

# Output public key in PEM format (only required for viewing)
$ openssl rsa -in private.pem -outform PEM -pubout -out public.pem
```
Place the generated keys under `license-server/resources` directory.

### Running the server

```sh
# Use leiningen to run the project. Go to the root folder and run
$ lein run
```
This should run the server at the URL `http://localhost:8080`. Open the URL in the browser and you can perform the following actions from the UI.
* View public key
* Generate license (a signed string) from the given text message. Signing uses the private key.
* Verify the license by providing the text message and the signed string. Verification uses the public key.

Private key must be kept private and should not be accessible to the users.

Written in Clojure with ❤ by Jaseem V V (a.k.a kadaj)  
Sunday 16 October 2016
