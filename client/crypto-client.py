import json
import requests
import argparse

def main(args):
    try:
        # Generate Key
        if args.generate:
            response = requests.post(args.server_url + '/api/generate/')

            if response.status_code != 200:
                # This means something went wrong.
                raise Exception('Post /api/ {}'.format(response.status_code))
            # Encrypt data
        else:
            if args.keyId is None:
                raise SystemExit("Please enter your key id")

            if args.encrypt:
                strData = ""
                if args.input:
                    with open(args.input, "rb") as file:
                        strData = file.read()
                    print(type(strData))
                    response = requests.post(args.server_url + '/api/encrypt',
                                             json={'data': strData, 'keyId': args.keyId})
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

            # Decrypt data
            elif args.decrypt:
                if args.encryptedData is None:
                    raise SystemExit("Please enter your encryptedData")
                response = requests.post(args.server_url + '/api/decrypt',
                                         json={'keyId': args.keyId,
                                               'encryptedData': args.encryptedData})
                # Error response
                if response.status_code != 200:
                    # This means something went wrong.
                    raise Exception('Post /api/decrypt/ {}'.format(response.status_code))

            # Sign data
            elif args.sign:
                if args.data is None:
                    raise SystemExit("Please enter your data")
                response = requests.post(args.server_url + '/api/sign',
                                         json={'keyId': args.keyId,
                                               'data': args.data})

                if response.status_code != 200:
                    # This means something went wrong.
                    raise Exception('Post /api/sign/ {}'.format(response.status_code))

            # Verify data
            elif args.verify:
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
            else:
                raise Exception('Error:The <OPERATION> is not exist')

        json_data = response.json()
        print("Status code:", response.status_code, "OK")
        json_formatted_str = json.dumps(json_data, indent=3)
        print(json_formatted_str)

    # Throw an exception if the server is not working
    except requests.exceptions.RequestException as e:
        raise SystemExit("The server is not available")

    # Throw an exception if haven't entered complete data
    except ValueError as e:
        raise Exception(e)


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Crypto Client")
    parser.add_argument("server_url", help="Server URL")
    parser.add_argument("-keyId", help="Key Id")
    parser.add_argument("-data", help="Data")
    parser.add_argument("-signature", help="Signature")
    parser.add_argument("-encryptedData", help="Encrypted Data")
    parser.add_argument("-generate", help="Generate Key", action="store_true")
    parser.add_argument("-encrypt", help="Encrypt data", action="store_true")
    parser.add_argument("-decrypt", help="Decrypt data", action="store_true")
    parser.add_argument("-sign", help="Sign data", action="store_true")
    parser.add_argument("-verify", help="Verify data", action="store_true")
    parser.add_argument("-input", help="Input file")

    args = parser.parse_args()

    main(args)
