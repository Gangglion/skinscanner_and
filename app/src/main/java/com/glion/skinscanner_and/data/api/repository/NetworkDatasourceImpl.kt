package com.glion.skinscanner_and.data.api.repository

import com.glion.skinscanner_and.data.api.data.AppVersion
import com.glion.skinscanner_and.data.api.data.DermatologyData
import com.glion.skinscanner_and.data.api.data.RequestCheckFile
import com.glion.skinscanner_and.data.api.data.RequestExchangeKey
import com.glion.skinscanner_and.data.api.mapper.toData
import com.glion.skinscanner_and.data.api.source.KakaoApiService
import com.glion.skinscanner_and.data.api.source.MyApiService
import com.glion.skinscanner_and.util.Utility
import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NetworkDatasourceImpl @Inject constructor(
    private val kakaoApiService : KakaoApiService,
    private val myApiService: MyApiService
) : NetworkDatasource {
    /**
     * 앱 버전 체크
     */
    override suspend fun getVersion(): Flow<Int?> = flow {
        val snapshot = Firebase.database.reference.get().await()
        val serverVersion = snapshot.getValue(AppVersion::class.java)
        if(serverVersion != null) {
            val flag = Utility.compareAppVersion(serverVersion.versionName, serverVersion.versionType)
            emit(flag)
        } else {
            emit(null)
        }
    }

    /**
     * 주변 3키로 내의 피부과 가져오기
     */
    override suspend fun getDermatologyList(
        query: String,
        categoryGroupCode: String,
        x: String,
        y: String,
        radius: Int,
        page: Int
    ) : Flow<DermatologyData> = flow {
        val response = kakaoApiService.searchKeyword(query, categoryGroupCode, x, y, radius, page)
        val dermatologyData = response.documents.let {
            it.map { document ->
                document.toData()
            }
        }
        val isEnd = response.meta.is_end
        emit(DermatologyData(dermatologyData, isEnd))
    }.flowOn(Dispatchers.IO)

    override suspend fun exchangeKey(request: RequestExchangeKey) = flow {
        val response = myApiService.exchangeKey(request)
        // note : key, iv 둘다 RSA 복호화 진행
        val key = response.key
        val iv = response.iv
        // TODO : key, iv 파일로 저장. 성공시 true 리턴
        emit(true)
    }.flowOn(Dispatchers.IO)

    override suspend fun checkFile(request: RequestCheckFile) = flow {
        val response = myApiService.checkFile(request)
        emit(response.needUpdate)
    }.flowOn(Dispatchers.IO)
}