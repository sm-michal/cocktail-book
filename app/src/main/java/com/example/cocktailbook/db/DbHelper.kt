package com.example.cocktailbook.db

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.cocktailbook.db.model.StorageIngredient
import com.example.cocktailbook.db.model.IngredientType
import com.example.cocktailbook.db.model.IngredientType.BASE_ALCOHOL
import com.example.cocktailbook.db.model.IngredientType.JUICE
import com.example.cocktailbook.db.model.IngredientType.ADDITIONAL

const val DATABASE_NAME = "CocktailRecipesDb"

class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1){
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("""
            create table  ingredient_types (
                id integer primary key,
                name text
            )
        """
        )
        db?.execSQL("""
            create table ingredients (
                id integer primary key autoincrement,
                type integer, 
                name text,
                foreign key(type) references ingredient_types(id)
            )
        """
        )
        db?.execSQL("""
            create table recipes (
                id integer primary key autoincrement,
                name text,
                description text
            )    
        """
        )
        db?.execSQL("""
            create table recipes_ingredients (
                recipe_id integer,
                ingredient_id integer,
                quantity number,
                unit text,
                foreign key(recipe_id) references recipes(id),
                foreign key(ingredient_id) references ingredients(id)
            )    
        """
        )

        db?.execSQL("""
            create table ingredients_in_storage (
                ingredient_id integer,
                foreign key(ingredient_id) references ingredients(id)
            )            
        """
        )

        initData(db)
    }

    private fun getIngredientId(name: String, db: SQLiteDatabase?): Long =
        DatabaseUtils.longForQuery(
            db,
            "select id from ingredients where name = ?", arrayOf(name)
        )

    private fun getRecipeId(name: String, db: SQLiteDatabase?): Long =
        DatabaseUtils.longForQuery(
            db,
            "select id from recipes where name = ?", arrayOf(name)
        )


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table recipe_ingredients")
        db?.execSQL("drop table recipes")
        db?.execSQL("drop table ingredients")
        db?.execSQL("drop table ingredient_types")
        onCreate(db)
    }

    fun getAllIngredientsByType(type: IngredientType): List<StorageIngredient> {
        with(readableDatabase.rawQuery("""
            select i.id, i.name, iis.ingredient_id from ingredients i 
            left join ingredients_in_storage iis on i.id = iis.ingredient_id  
            where type = ?
            """,
            arrayOf(type.id.toString()))) {
            moveToFirst()

            val result = ArrayList<StorageIngredient>()
            while (!isAfterLast) {
                result.add(StorageIngredient(getLong(0), type, getString(1), getString(2) != null))
                moveToNext()
            }
            return result
        }
    }

    private fun initData(db: SQLiteDatabase?) {
        // some init data
        db?.insert("ingredient_types", null, ContentValues().apply {
            put("id", 1)
            put("name", "Alkohol bazowy")
        })
        db?.insert("ingredient_types", null, ContentValues().apply {
            put("id", 2)
            put("name", "Likier")
        })
        db?.insert("ingredient_types", null, ContentValues().apply {
            put("id", "3")
            put("name", "Sok")
        })
        db?.insert("ingredient_types", null, ContentValues().apply {
            put("id", "4")
            put("name", "Dodatek")
        })


        db?.insert("ingredients", null, ContentValues().apply {
            put("type", BASE_ALCOHOL.id)
            put("name", "Wódka")
        })
        db?.insert("ingredients", null, ContentValues().apply {
            put("type", BASE_ALCOHOL.id)
            put("name", "Rum")
        })
        db?.insert("ingredients", null, ContentValues().apply {
            put("type", BASE_ALCOHOL.id)
            put("name", "Gin")
        })
        db?.insert("ingredients", null, ContentValues().apply {
            put("type", JUICE.id)
            put("name", "Cola")
        })
        db?.insert("ingredients", null, ContentValues().apply {
            put("type", ADDITIONAL.id)
            put("name", "Limonka")
        })

        db?.insert("recipes", null, ContentValues().apply {
            put("name", "Cuba Libre")
            put("description", "Do szklanki z lodem wlewam rum, dopełniam colą i wyciskam sok z dwóch ćwiartek limonki, lekko mieszam")
        })
        db?.insert("recipes", null, ContentValues().apply {
            put("name", "Daiquiri")
            put("description", "Do szklanicy wlewam rum i sok z limonki, wstrząsam z lodem i odcedzam do schłodzonego kieliszka")
        })

        db?.insert("recipes_ingredients", null, ContentValues().apply {
            put("recipe_id", getRecipeId("Cuba Libre", db))
            put("ingredient_id", getIngredientId("Rum", db))
            put("quantity", 40)
            put("unit", "ml")
        })
        db?.insert("recipes_ingredients", null, ContentValues().apply {
            put("recipe_id", getRecipeId("Cuba Libre", db))
            put("ingredient_id", getIngredientId("Cola", db))
            put("quantity", 150)
            put("unit", "ml")
        })
        db?.insert("recipes_ingredients", null, ContentValues().apply {
            put("recipe_id", getRecipeId("Cuba Libre", db))
            put("ingredient_id", getIngredientId("Limonka", db))
            put("quantity", 0.5)
            put("unit", "")
        })

        db?.insert("recipes_ingredients", null, ContentValues().apply {
            put("recipe_id", getRecipeId("Daiquiri", db))
            put("ingredient_id", getIngredientId("Rum", db))
            put("quantity", 80)
            put("unit", "ml")
        })
        db?.insert("recipes_ingredients", null, ContentValues().apply {
            put("recipe_id", getRecipeId("Cuba Libre", db))
            put("ingredient_id", getIngredientId("Limonka", db))
            put("quantity", 1)
            put("unit", "")
        })
    }

    fun saveNewIngredientInStorage(ingredientId: Long) {
        writableDatabase.insert("ingredients_in_storage", null, ContentValues().apply {
            put("ingredient_id", ingredientId)
        })
    }

    fun removeFromStorage(ingredientId: Long) {
        writableDatabase.delete("ingredients_in_storage", "ingredient_id = ?", arrayOf(ingredientId.toString()))
    }
}