import json
import base64
import requests
import argparse


def print_json(response):
    json_data = response.json()
    print("Status code:", response.status_code, "OK")
    print(json_data['keyId'])
    json_formatted_str = json.dumps(json_data, indent=3)
    print(json_formatted_str)


# Generate keyId method
def generate(args):
    response = requests.post(args.server_url + '/api/generate/')
    if response.status_code != 200:
        # This means something went wrong.
        raise Exception('Post /api/ {}'.format(response.status_code))

        # Print the result of generate keyId
    json_data = response.json()
    print("Status code:", response.status_code, "OK")
    print("A KeyId was generated,\nThe KeyId is:", json_data['keyId'])


# Encrypt method
def encrypt(args):
    if args.input:
        with open(args.input, "rb") as file:
            data = file.read()
        str_data = base64.b64encode(data)
        str_data= str_data.decode("utf-8")
        print(type(str_data))

        response = requests.post(args.server_url + '/api/encrypt',
                                 json={'data': str_data, 'keyId': args.keyId})
        # print(response.json()['EncryptData'])
        # args.output.write(response.json()['EncryptData'])

        # with open(args.output, "w") as file:
        #     output = file.write(response.json()['EncryptData'])

    else:
        if args.data is None:
            raise SystemExit("Please enter your data")
        response = requests.post(args.server_url + '/api/encrypt',
                                 json={'keyId': args.keyId,
                                       'data': args.data})
        # Error response
    if response.status_code != 200:
        # This means something went wrong.
        raise Exception('Post /api/encrypt/ {}'.format(response.status_code))

        # Print the result of encrypted data
    json_data = response.json()
    print("Status code:", response.status_code, "OK")
    print("A data was encrypted,\nThe data encrypt is:", json_data['EncryptData'])


# Decrypt method
def decrypt(args):
    if args.encryptedData is None:
        raise SystemExit("Please enter your encryptedData")

    response = requests.post(args.server_url + '/api/decrypt',
                             json={'keyId': args.keyId,
                                   'encryptedData': args.encryptedData})


    # Error response
    if response.status_code != 200:
        # This means something went wrong.
        raise Exception('Post /api/decrypt/ {}'.format(response.status_code))

    # Print the result of decrypted data
    json_data = response.json()['DecryptData']

    if args.output:
        json_data = json_data.encoded()
        f = open(args.output, "w").write(json_data)

    print("Status code:", response.status_code, "OK")
    print("A data was decrypted,\nThe data is:", json_data)


# Sign method
def sign(args):
    if args.data is None:
        raise SystemExit("Please enter your data")

    response = requests.post(args.server_url + '/api/sign',
                             json={'keyId': args.keyId,
                                   'data': args.data})

    if response.status_code != 200:
        # This means something went wrong.
        raise Exception('Post /api/sign/ {}'.format(response.status_code))

    # Print the result of sign data
    json_data = response.json()
    print("Status code:", response.status_code, "OK")
    print("A data was signature,\nThe signature data is:", json_data['sign'])


# Verify method
def verify(args):
    if args.data is None:
        raise SystemExit("Please enter your data")
    if args.signature is None:
        raise SystemExit("Please enter your signature")

    response = requests.post(args.server_url + '/api/verify',
                             json={'keyId': args.keyId,
                                   'data': args.data,
                                   'signature': args.signature})

    if response.status_code != 200:
        # This means something went wrong.
        raise Exception('Post /api/verify/ {}'.format(response.status_code))

    # Print the result of verify data
    data = response.text
    print("Status code:", response.status_code, "OK")
    print("A data was verified,\nThe result of verify data is:", data)


def main(args):
    try:
        # Generate Key
        if args.generate:
            generate(args)

        else:
            # if key id isn't exist
            if args.keyId is None:
                raise SystemExit("Please enter your key id")

            # choose the operation
            fmt = args.operation

            # Encrypt data
            if fmt == 'encrypt':
                encrypt(args)

            # Decrypt data
            elif fmt == 'decrypt':
                decrypt(args)

            # Sign data
            elif fmt == 'sign':
                sign(args)

            # Verify data
            elif fmt == 'verify':
                verify(args)

            else:
                raise Exception('Error:The <OPERATION> is not exist')

    # Throw an exception if the server is not working
    except requests.exceptions.RequestException:
        raise SystemExit("The server is not available")

    # Throw an exception if haven't entered complete data
    except ValueError as e:
        raise Exception(e)


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Crypto Client")
    parser.add_argument("server_url", help="Server URL")
    parser.add_argument("-keyId", help="Key Id")
    parser.add_argument("-data", help="Data")
    parser.add_argument("-encryptedData", help="Encrypted Data")
    parser.add_argument("-generate", help="Generate Key", action="store_true")
    parser.add_argument("-signature", help="Signature")
    parser.add_argument("-input", help="Input file")
    parser.add_argument("-output", help="Output file")
    parser.add_argument("-operation", help="choose the operation",
                        choices=['encrypt', 'decrypt', 'sign', 'verify'])
    argument = parser.parse_args()

    main(argument)
