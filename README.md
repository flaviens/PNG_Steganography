# PNG_Steganography

This project explores a way to hide encrypted invisible messages into a PNG image.

## Usage

First, open an existing PNG file. If a hidden message has already been put by the app, it will appear.
Then, type the message that you would like to hide into the PNG file. Optionally, you can encrypt it with AES.
Finally, save the file as a new PNG file. This file will then contain the hidden message!

Only a person who has the AES secret key and knows that and how the steganography has been performed can read the message.
Since the image does not get visually changed, the hidden message stays discrete, as long as its size remains reasonable.

## How it works

PNG images are made of chunks of data, and some of them can be made optional. This makes the format extremely flexible, and makes relatively easy the process of adding new chunks that hide some data.
The application thus hides the optionally encrypted message into a special chunk at a special location.
