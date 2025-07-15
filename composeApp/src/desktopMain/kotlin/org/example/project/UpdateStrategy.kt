package org.example.project

interface UpdateStrategy {
    fun execute(updateData: UpdateData)
    fun validateUpdate(updateData: UpdateData)
}