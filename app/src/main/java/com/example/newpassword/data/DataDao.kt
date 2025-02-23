package com.example.newpassword.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
abstract class PasswordInfoDao {
    /**
     * 查询所有数据
     */
    @Query("select * from password_info_table ")
    abstract fun getAll(): List<PasswordItemInfo>

    /**
     * 查询所有数据
     */
    @Query("select * from password_info_table ")
    abstract fun getFlowAll(): Flow<List<PasswordItemInfo>>


    /**
     * 根据id查询
     */
    @Query("select * from password_info_table where id==:id")
    abstract fun getInfoById(id: Int): PasswordItemInfo
    fun getDecryptedById(id: Int): PasswordItemInfo {
        val item = getInfoById(id)
        val decryptedPassword = AesEncryptionUtil.decryptWithKey(item.password)
        if (decryptedPassword != null) {
            item.password = decryptedPassword
        }
        return item
    }

    /**
     * 根据应用名查询
     */
    @Query("select *  from password_info_table where appName==:appName")
    abstract fun getNumByName(appName: String): List<PasswordItemInfo>

    fun getDecryptedByName(appName: String): List<PasswordItemInfo> {
        return getNumByName(appName).map { item ->
            val decryptedPassword = AesEncryptionUtil.decryptWithKey(item.password)
            if (decryptedPassword != null) {
                item.password = decryptedPassword
            }
            item
        }
    }

    /**
     * 查询所有数据
     */
    @Query("select appName, COUNT( id ) as number from password_info_table where appName like '%'||:appName||'%' GROUP BY appName ORDER BY appName")
    abstract fun getFlowList(appName: String): Flow<List<AccountNumberInfo>>


    /**
     * 插入数据
     */
    @Insert
    abstract suspend fun insertPasswordItemInfo(passwordItemInfo: PasswordItemInfo)

    suspend fun insertEncryptedPasswordItemInfo(passwordItemInfo: PasswordItemInfo) {
        val encryptedPassword = AesEncryptionUtil.encryptWithKey(passwordItemInfo.password)
        if (encryptedPassword != null) {
            passwordItemInfo.password = encryptedPassword
            insertPasswordItemInfo(passwordItemInfo)
        }
    }

    /**
     * 插入数据
     */
    @Insert
    abstract fun insertPasswordItemInfoNonSuspend(passwordItemInfo: PasswordItemInfo)


    /**
     * 删除
     */
    @Delete
    abstract suspend fun delete(passwordItemInfo: PasswordItemInfo)

    /**
     * 更新数据
     */
    @Update
    abstract suspend fun updatePasswordItemInfo(passwordItemInfo: PasswordItemInfo)
    suspend fun updateEncryptedPasswordItemInfo(passwordItemInfo: PasswordItemInfo) {
        val encryptedPassword = AesEncryptionUtil.encryptWithKey(passwordItemInfo.password)
        if (encryptedPassword != null) {
            passwordItemInfo.password = encryptedPassword
            updatePasswordItemInfo(passwordItemInfo)
        }
    }

    /**
     * 根据应用名查询
     */
    @Query("SELECT * FROM password_info_table WHERE appName LIKE :appName")
    abstract fun searchPasswords(appName: String): Flow<List<PasswordItemInfo>>


    /**
     * 加密所有数据
     */
    suspend fun encryptAll() {
        val all = getAll()
        all.forEach { item ->
            val encryptedPassword = AesEncryptionUtil.encryptWithKey(item.password)
            updatePasswordItemInfo(item.copy(password = encryptedPassword))
        }
    }

    /**
     * 解密所有数据
     */
    suspend fun decryptAll() {
        val all = getAll()
        all.forEach { item ->
            val encryptedPassword = AesEncryptionUtil.decryptWithKey(item.password)
            updatePasswordItemInfo(item.copy(password = encryptedPassword))
        }
    }
}
