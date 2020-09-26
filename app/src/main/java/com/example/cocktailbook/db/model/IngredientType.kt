package com.example.cocktailbook.db.model

import java.lang.Exception

enum class IngredientType(val id: Int) {
    BASE_ALCOHOL(1),
    LIQUOR(2),
    JUICE(3),
    ADDITIONAL(4);

    companion object {
        fun getById(id: Int) =
            values()
                .find { it.id == id }
                ?: throw Exception("Incorrect IngredientTypeId")
    }
}