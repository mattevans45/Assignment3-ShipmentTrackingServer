package org.example.project

interface UpdateStrategy {
    suspend fun execute(updateData: UpdateData)
    fun validateUpdate(updateData: UpdateData)
    fun getUpdateType(): String
}