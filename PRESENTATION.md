# This document is required.

What is AES?

The Advanced Encryption Standard (AES), or Rijndael was developed by two Belgian cryptographers, Joan Daemen and Vincent Rijmen. 

History of AES/
Before AES:/
There used to be a standard called the Data Encryption Standard (DES), implemented in 1977. However, by the late 1990s, it was starting to become outdated and people were able to brute force attack DES and crack it.

So, the U.S. National Institute of Standards and Technology (NIST) started a competition to find a replacement. After 15 candidates and 5 finalist, the Rijndael algorithm won the competition and became AES.

There are 3 different versions of AES encryption, each has a block size of 128 bits, but with different key lengths: 128, 192 and 256 bits.

AES has been adopted by the U.S. government.

AES is included in the ISO/IEC 18033-3 standard. AES became effective as a U.S. federal government standard on May 26, 2002, after approval by U.S. Secretary of Commerce Donald Evans. AES is available in many different encryption packages, and is the first (and only) publicly accessible cipher approved by the U.S. National Security Agency (NSA) for top secret information when used in an NSA approved cryptographic module

How does AES work?

This part of the presentation will cover the first 2 of 4 steps of AES encryption, SubBytes and ShiftRows. 

The SubBytes step goes first. It uses a lookup table like the vigenere cipher./
![AES_S-box](AES_S-box.png)/
lets say you have to encrypt the number 53, that would convert into ed./

The next step is the ShiftRows step./
It takes a 4 by 4 row, and shifts each row to the left by a certain amount:/
the first row shifts by nothing/
the second row shifts to the left by 1/
the third row shifts to the left by 2/
the fourth row shifts to the left by 3