#include <jni.h>
#include <stdio.h>
#include "com_vvote_verifierlibrary_utils_crypto_CryptoUtils.h"
#include <cstring>
#include <iostream>
#include <iomanip>
#include <sstream>
#include <string>
#include <cstdlib>

using namespace std;

#include <openssl/sha.h>

/*
 * Converts from a java string (jstring) to an std string
*/
 void GetJStringContent(JNIEnv *AEnv, jstring AStr, std::string &ARes) {
  if (!AStr) {
    ARes.clear();
    return;
  }

  const char *s = AEnv->GetStringUTFChars(AStr,NULL);
  ARes=s;
  AEnv->ReleaseStringUTFChars(AStr,s);
}

void hexStringToASCIIString(const string &inputHexString, string &outputASCIIString) {
  int input_len = inputHexString.length();
  for(int i=0; i< input_len; i+=2)
  {
    string byte = inputHexString.substr(i,2);
    char chr = (char) (int)strtol(byte.c_str(), NULL, 16);
    outputASCIIString.push_back(chr);
  }
}

/*
 * Carries out a verification on the hash commitment made using the provided witness and randomness value
*/
JNIEXPORT jboolean JNICALL Java_com_vvote_verifierlibrary_utils_crypto_CryptoUtils_openSSLVerifyHashCommitment
  (JNIEnv *env, jclass jClass, jstring commitment, jstring witness, jstring random){
  
  // convert from jstring to std string
  // these values will contain the hexadecimal strings
  string commitValue;
  string witnessValue;
  string randomValue;
  GetJStringContent(env, commitment, commitValue);
  GetJStringContent(env, witness, witnessValue);
  GetJStringContent(env, random, randomValue);
  
  // convert from hex string to ascii string
  std::string witnessValueString;
  hexStringToASCIIString(witnessValue, witnessValueString);
  
  std::string randomValueString;
  hexStringToASCIIString(randomValue, randomValueString);
  
  // convert from std string to unsigned char *
  const unsigned char * wit = reinterpret_cast<const unsigned char *> (witnessValueString.c_str());
  const unsigned char * rand = reinterpret_cast<const unsigned char *> (randomValueString.c_str());
  
  char combined[(SHA256_DIGEST_LENGTH * 2) + 1];
  unsigned char md[SHA256_DIGEST_LENGTH];
  unsigned char modifiedRand[SHA256_DIGEST_LENGTH];
  const unsigned char * m_rand;
  
  // initialise
  SHA256_CTX context;
  if(!SHA256_Init(&context)){
    return false;
  }
  
  // make sure the random value length is less than or equal to 32 otherwise hash it first
  if(randomValueString.length() > 32){
	if(!SHA256_Update(&context, rand, randomValueString.length())){
		return false;
	}
	if(!SHA256_Final(modifiedRand, &context)){
		return false;
	}
	// reinitialise the context
	if(!SHA256_Init(&context)){
		printf("3");
		return false;
	}
	m_rand = modifiedRand;
  }else{
	  m_rand = rand;
  }

  // first update with the witness
  if(!SHA256_Update(&context, wit, SHA256_DIGEST_LENGTH)){
    return false;
  }
  
  // second update with the random value
  if(!SHA256_Update(&context, m_rand, SHA256_DIGEST_LENGTH)){
    return false; 
  }
  
  // perform the hash calculation
  if(!SHA256_Final(md, &context)){
    return false;
  }
  
  // convert from ascii string to hex string
  for(int i = 0; i < SHA256_DIGEST_LENGTH; i++)
  {
    sprintf(combined + (i * 2), "%02x", md[i]);
  }
  combined[SHA256_DIGEST_LENGTH * 2] = 0;
  
  jboolean result = false;
  
  // check that the combined hash value matches the commitment value
  if(combined == commitValue){
	result = true;
  }else{
	// check whether we need to convert to uppercase before failing
	char c;
	int i=0;
	while (combined[i]){
		c=combined[i];
		combined[i] = toupper(c);
		i++;
	}	
	if(combined == commitValue){
		result = true;
	}  
  }
	
  return result;
  }
