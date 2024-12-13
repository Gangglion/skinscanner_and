package com.glion.skinscanner_and.util

import android.content.Context
import android.os.Build
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Scanner

class RootCheck (
    private val mContext: Context
) {
    // 루팅체크
    fun checkSu(): Boolean {
        Log.i(
            RootCheck::class.java.simpleName,
            " ___    _____  _____  _____  _  _   _  ___            ___    _   _  ___    ___    _   _ \n" +
                    "|  _`\\ (  _  )(  _  )(_   _)(_)( ) ( )(  _`\\         (  _`\\ ( ) ( )(  _`\\ (  _`\\ ( ) ( )\n" +
                    "| (_) )| ( ) || ( ) |  | |  | || `\\| || ( (_)        | ( (_)| |_| || (_(_)| ( (_)| |/'/'\n" +
                    "| ,  / | | | || | | |  | |  | || , ` || |___         | |  _ |  _  ||  _)_ | |  _ | , <  \n" +
                    "| |\\ \\ | (_) || (_) |  | |  | || |`\\ || (_, )        | (_( )| | | || (_( )| (_( )| |\\`\\ \n" +
                    "(_) (_)(_____)(_____)  (_)  (_)(_) (_)(____/'        (____/'(_) (_)(____/'(____/'(_) (_)\n" +
                    "                                              ______                                    \n" +
                    "                                             (______)                                   \n"
        )
        return checkRootedFiles() || checkRootPackages() || checkTags() || checkSuperUserCommand() || checkSuperUserCommand2() || checkOTACerts() || checkDangerousAppsPackages() || checkForDangerousProps()
    }

    /**
     * 루팅된 파일 체크
     */
    private fun checkRootedFiles(): Boolean {
        val files = arrayOf(
            "/cache/su",
            "/data/su",
            "/data/local/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/dev/su",
            "/sbin/su",
            "/system/app/su.apk",
            "/system/app/Superuser.apk",
            "/system/su",
            "/system/bin/su",
            "/system/bin/.ext/su",
            "/system/bin/failsafe/su",
            "/system/sbin/su",
            "/system/xbin/su",
            "/system/xbin/mu",
            "/system/sd/xbin/su",
            "/system/usr/su-backup",
            "/su/bin/su",
            "/su/bin",
            "/cache/busybox",
            "/data/busybox",
            "/data/local/busybox",
            "/data/local/bin/busybox",
            "/data/local/xbin/busybox",
            "/dev/busybox",
            "/sbin/busybox",
            "/system/bin/busybox",
            "/system/bin/failsafe/busybox",
            "/system/sd/xbin/busybox",
            "/system/xbin/busybox",
            "/system/bin/.ext",
            "/system/xbin/.ext",
            "system/xbin/daemonsu",
            "system/usr/we-need-root/",
            "/system/bin/.ext/.su",
            "/system/usr/we-need-root/su-backup"
        )
        for (s in files) {
            val file = File(s)
            if (file.exists()) {
                Log.d(
                    RootCheck::class.java.simpleName,
                    "Rooted File :: " + file.absolutePath + ":" + file.name
                )
                return true
            }
        }
        return false
    }

    /**
     * 루팅 앱 패키지 체크
     */
    private fun checkRootPackages(): Boolean {
        val pm = mContext.packageManager
        val rootPackages = arrayOf(
            "com.devadvance.rootcloak",
            "com.devadvance.rootcloakplus",
            "com.koushikdutta.superuser",
            "com.thirdparty.superuser",
            "eu.chainfire.supersu",
            "de.robv.android.xposed.installer",
            "com.saurik.substrate",
            "com.zachspong.temprootremovejb",
            "com.amphoras.hidemyroot",
            "com.amphoras.hidemyrootadfree",
            "com.formyhm.hiderootPremium",
            "com.formyhm.hideroot",
            "com.noshufou.android.su",
            "com.noshufou.android.su.elite",
            "com.yellowes.su",
            "com.topjohnwu.magisk",
            "com.kingroot.kinguser",
            "com.kingo.root",
            "com.smedialink.oneclickroot",
            "com.zhiqupk.root.global",
            "com.alephzain.framaroot",
            "com.noshufou.android.su"
        )
        if (pm != null) {
            for (pkg in rootPackages) {
                try {
                    pm.getPackageInfo(pkg, 0)
                    return true
                } catch (ignored: Exception) {
                } catch (ignored: Error) {
                }
            }
        }
        return false
    }

    /**
     * 루팅된 기기에서 흔히 발견되는 앱 패키지 확인. 나열된 앱 목록은 루팅된 기기에서 흔히 사용하는 응용프로그램
     */
    private fun checkDangerousAppsPackages() : Boolean {
        val pm = mContext.packageManager
        val dangerousAppsPackages = arrayOf(
            "com.koushikdutta.rommanager",
            "com.koushikdutta.rommanager.license",
            "com.dimonvideo.luckypatcher",
            "com.chelpus.lackypatch",
            "com.ramdroid.appquarantine",
            "com.ramdroid.appquarantinepro",
            "com.android.vending.billing.InAppBillingService.COIN",
            "com.android.vending.billing.InAppBillingService.LUCK",
            "com.chelpus.luckypatcher",
            "com.blackmartalpha",
            "org.blackmart.market",
            "com.allinone.free",
            "com.repodroid.app",
            "org.creeplays.hack",
            "com.baseappfull.fwd",
            "com.zmapp",
            "com.dv.marketmod.installer",
            "org.mobilism.android",
            "com.android.wp.net.log",
            "com.android.camera.update",
            "cc.madkite.freedom",
            "com.solohsu.android.edxp.manager",
            "org.meowcat.edxposed.manager",
            "com.xmodgame",
            "com.cih.game_cih",
            "com.charles.lpoqasert",
            "catch_.me_.if_.you_.can_"
        )
        if (pm != null) {
            for (pkg in dangerousAppsPackages) {
                try {
                    pm.getPackageInfo(pkg, 0)
                    return true
                } catch (ignored: Exception) {
                } catch (ignored: Error) {
                }
            }
        }
        return false
    }

    /**
     * 루팅이 된 기기는 일반적으로 Build.TAGS 값이 제조사 키값이 아닌 test 키 값을 가지고 있다.
     */
    private fun checkTags(): Boolean {
        val buildTags = Build.TAGS
        Log.d(RootCheck::class.java.simpleName, "Build Tags :: " + buildTags!!.contains("test-key"))
        return buildTags.contains("test-key")
    }

    /**
     * super user 커맨드 체크
     */
    private fun checkSuperUserCommand(): Boolean {
        try {
            Runtime.getRuntime().exec("su")
            Log.d(RootCheck::class.java.simpleName, "Device has super user")
            return true
        } catch (ignored: Error) {
        } catch (ignored: Exception) {
        }
        return false
    }

    /**
     * super user 커맨드 체크 2
     */
    private fun checkSuperUserCommand2(): Boolean {
        var process: Process? = null
        return try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val `in` = BufferedReader(InputStreamReader(process.inputStream))
            `in`.readLine() != null
        } catch (t: Throwable) {
            false
        } finally {
            process?.destroy()
        }
    }

    /**
     * Google OTA Certificates - 구글 OTA 인증서 빌드 태그 확인(안드로이드 빌드인지, 사용자 지정 ROM 인지 확인)
     * otaPath 경로의 파일이 존재하지 않으면 사용자 지정 ROM 으로 판단(루트된 기기 판단)
     * @return 사용자 지정 ROM 일 경우 true
     */
    private fun checkOTACerts() : Boolean {
        val otaPath = "/etc/security/otacerts.zip"
        val f = File(otaPath)
        return !(f.exists())
    }

    /**
     * build.prob 확인 -
     * 일부 사용자 지정 빌드/루트 앱은 Android 장치의 build.prop을 수정하고 루트 사용자만 수행할 수 있는 일부 속성을 수정한다.
     * 앱에서 아래 두 함수를 사용하여 속성이 수정되었는지 여부를 감지할 수 있음. 일반적으로 수정되는 일부 속성은 ro.debuggable 및 ro.secure.ui 임
     */
    private fun propsReader() : List<String>? {
        try {
            val inputStream: InputStream = Runtime.getRuntime().exec("getprop").inputStream ?: return null
            val probVal = Scanner(inputStream).useDelimiter("\\A").next()
            return probVal.split("\n")
        } catch(e: Exception) {
            when(e) {
                is IOException, is NoSuchElementException -> {
                    Log.e(RootCheck::class.java.simpleName, "IOException", e)
                }
            }
            return null
        }
    }

    private fun checkForDangerousProps() : Boolean {
        val dangerousProbs = mapOf(
            "ro.debuggable" to "1",
            "ro.secure" to "0"
        )
        var result = false
        val lines = propsReader() ?: return false
        for(line in lines) {
            for((key, badValue) in dangerousProbs) {
                if(line.contains(key)) {
                    val formattedBadValue = "[$badValue]"
                    if(line.contains(formattedBadValue)) {
                        Log.v(RootCheck::class.java.simpleName, "$key = $formattedBadValue detected!")
                        result = true
                    }
                }
            }
        }
        return result
    }
}