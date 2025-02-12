package com.glion.skinscanner_and.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.glion.skinscanner_and.data.api.data.AESKey
import com.google.android.gms.common.util.Base64Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.MGF1ParameterSpec
import java.security.spec.RSAKeyGenParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

object CryptoUtils {
    private var privateKey: PrivateKey? = null
    private var publicKey: PublicKey? = null
    private var aesKey: AESKey? = null

    fun getAESKey(): AESKey?{
        return aesKey
    }

    suspend fun rsaInitialize() {
        withContext(Dispatchers.IO) {
            getKeyInKeyStore()
        }
//        val deferredRsaInit = CoroutineScope(Dispatchers.IO).async{
//            getKeyInKeyStore()
//        }
//        deferredRsaInit.await()
    }

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply{
        load(null)
    }

    /**
     * 키스토어 내에 별칭으로 지정한 키가 존재하는지 확인 후 있다면 가져오고, 없다면 생성
     */
    private suspend fun getKeyInKeyStore() {
        withContext(Dispatchers.IO) {
            if(keyStore.containsAlias(Define.KEY_ALIAS)){
                LogUtil.i("키 존재")
                val entry = keyStore.getEntry(Define.KEY_ALIAS, null)
                if(entry is KeyStore.PrivateKeyEntry){
                    privateKey = entry.privateKey
                    publicKey = entry.certificate.publicKey
                }
            } else{
                LogUtil.i("키 존재하지않음. 생성")
                makeRsaKey()
            }
        }
    }

    /**
     * 키 쌍 생성, KeyStore에 저장
     */
    private suspend fun makeRsaKey(){
        withContext(Dispatchers.IO) {
            val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")
            val parameterSpec = KeyGenParameterSpec.Builder(
                Define.KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).run{
                setAlgorithmParameterSpec(
                    RSAKeyGenParameterSpec(
                        4096,
                        RSAKeyGenParameterSpec.F4
                    )
                )
                setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                setDigests(KeyProperties.DIGEST_SHA1)
                setUserAuthenticationRequired(false)
                build()
            }
            keyPairGenerator.initialize(parameterSpec)
            val keyPair = keyPairGenerator.generateKeyPair()
            publicKey = keyPair.public
            privateKey = keyPair.private
        }
//        val deferred = CoroutineScope(Dispatchers.IO).async{
//            val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")
//            val parameterSpec = KeyGenParameterSpec.Builder(
//                Define.KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
//            ).run{
//                setAlgorithmParameterSpec(
//                    RSAKeyGenParameterSpec(
//                        4096,
//                        RSAKeyGenParameterSpec.F4
//                    )
//                )
//                setBlockModes(KeyProperties.BLOCK_MODE_ECB)
//                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
//                setDigests(KeyProperties.DIGEST_SHA1)
//                setUserAuthenticationRequired(false)
//                build()
//            }
//            keyPairGenerator.initialize(parameterSpec)
//            val keyPair = keyPairGenerator.generateKeyPair()
//            publicKey = keyPair.public
//            privateKey = keyPair.private
//        }
//        deferred.await()
    }

    /**
     * key to PEM
     */
    fun publicKeyToPEM(): String{
        var pemString = ""
        val keyBase64: String = Base64.encodeToString(publicKey!!.encoded, Base64.NO_WRAP)
        pemString = keyBase64.chunked(64).joinToString(
            separator = "\n",
            prefix = "-----BEGIN PUBLIC KEY-----\n",
            postfix = "\n-----END PUBLIC KEY-----\n"
        )
        return pemString;
    }

    /**
     * 서버 전송용으로 pemString을 전처리
     */
    fun changeToPemForSend(pemString: String): String{
        return pemString
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\n","")
            .replace(" ", "")
    }

    /**
     * RSA 암호화
     */
    fun rsaEncrypt(text: String) : String?{
        val oappSp = OAEPParameterSpec(
            "SHA-1",
            "MGF1",
            MGF1ParameterSpec.SHA1,
            PSource.PSpecified.DEFAULT
        )
        val encCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding")
        encCipher.init(Cipher.ENCRYPT_MODE, publicKey, oappSp)
        val encryptTextByteArray = encCipher.doFinal(text.toByteArray())
        return Base64Utils.encode(encryptTextByteArray)
    }

    /**
     * RSA 복호화
     */
    fun rsaDecrypt(en: String) : String?{
        val oappSp = OAEPParameterSpec(
            "SHA-1",
            "MGF1",
            MGF1ParameterSpec.SHA1,
            PSource.PSpecified.DEFAULT
        )
        val decCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding")
        decCipher.init(Cipher.DECRYPT_MODE, privateKey, oappSp)
        var decryptTextByteArray = decCipher.doFinal(Base64Utils.decode(en))
        return String(decryptTextByteArray)
    }

    /**
     * RSA 암복호화 테스트
     */
    fun testRSAEncryption(){
        LogUtil.v("<공개키 PEM>\n${publicKeyToPEM()}")
        val text = "RSA 암복호화 테스트 String 0123"
        val en = rsaEncrypt(text)
        val de = rsaDecrypt(en!!)
        if(de == text){
            LogUtil.d("RSA 암복호화 테스트 성공")
        } else{
            LogUtil.d("RSA 암복호화 테스트 실패")
        }
    }
}