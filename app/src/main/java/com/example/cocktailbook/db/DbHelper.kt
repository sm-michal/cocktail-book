package com.example.cocktailbook.db

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.cocktailbook.Ingredient
import com.example.cocktailbook.db.model.StorageIngredient
import com.example.cocktailbook.db.model.IngredientType
import com.example.cocktailbook.db.model.IngredientType.BASE_ALCOHOL
import com.example.cocktailbook.db.model.IngredientType.JUICE
import com.example.cocktailbook.db.model.IngredientType.ADDITIONAL
import com.example.cocktailbook.db.model.Recipe
import com.example.cocktailbook.db.model.RecipeIngredient

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
        db?.execSQL("drop table ingredients_in_storage")
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

    fun getRecipes(): List<Recipe> {
        with(readableDatabase.rawQuery("""
            with available as (
                select distinct ri.recipe_id from recipes_ingredients ri 
                join ingredients_in_storage si on ri.ingredient_id = si.ingredient_id
                where not exists (
                    select 1 from recipes_ingredients rei 
                    where rei.recipe_id = ri.recipe_id and rei.ingredient_id not in (
                        select ingredient_id from ingredients_in_storage
                    ) 
                )
            )
            select r.id, r.name, r.description, 
            case when recipe_id is not null then 1 else 0 end is_available
            from recipes r
            left join available on r.id = available.recipe_id
            order by is_available desc, r.name
        """, emptyArray()
            )
        ) {

            moveToFirst()

            val result = arrayListOf<Recipe>()
            while (!isAfterLast) {
                val recipe = Recipe(getLong(0), getString(1), getString(2), getInt(3) == 1)

                recipe.ingredients.addAll(loadRecipeIngredients(recipe.id))

                result.add(recipe)

                moveToNext()
            }
            return result
        }
    }

    fun findMostValuableIngredientToBuy() {
        with(readableDatabase.rawQuery("""
            with missing as (
                select rei.recipe_id, rei.ingredient_id from recipes_ingredients rei
                left join ingredients_in_storage iis on iis.ingredient_id = rei.ingredient_id
                where iis.ingredient_id is null
            ),
            grouped as (
                select recipe_id, count(ingredient_id) as count_missing, group_concat(ingredient_id) as ing_ids from missing
                group by recipe_id
                order by 2      
            ),
            min_missing as (
                select ing_ids, count(recipe_id) as avail_recipes_count, group_concat(recipe_id) as recipes from grouped 
                where count_missing = (select min(count_missing) from grouped)
                group by ing_ids
                order by 2 desc
            )
            select ing_ids, avail_recipes_count, recipes from min_missing 
            where avail_recipes_count = (select max(avail_recipes_count) from min_missing)
        """, emptyArray())) {
            moveToFirst()

            while (!isAfterLast) {
                Log.i("DBHELPER", "${getString(0)} ${getLong(1)} ${getString(2)}")

                val ingNames = getString(0).split(",").map { it.toLong() }.map { getIngredientById(it) }.map { it?.name }
                val recipeNames = getString(2).split(",").map { it.toLong() }.map { getRecipeById(it) }.map { it?.name }
                Log.i("DBHELPER", "$ingNames $recipeNames")


                moveToNext()
            }
        }
    }

    private fun loadRecipeIngredients(recipeId: Long): List<RecipeIngredient> {
        with(readableDatabase.rawQuery("""
            select i.id, i.name, ri.quantity, ri.unit
            from recipes_ingredients ri join ingredients i on i.id = ri.ingredient_id
            where ri.recipe_id = ?
        """, arrayOf(recipeId.toString()))) {
            moveToFirst()

            val result = arrayListOf<RecipeIngredient>()
            while (!isAfterLast) {
                result.add(RecipeIngredient(recipeId, getLong(0), getString(1), getDouble(2), getString(3)))
                moveToNext()
            }
            return result
        }
    }

    private fun getRecipeById(recipeId: Long): Recipe? {
        with(readableDatabase.query("recipes", arrayOf("id", "name", "description"), "id = ?", arrayOf(recipeId.toString()), null, null, null)) {
            moveToFirst()

            return if (!isAfterLast)
                 Recipe(getLong(0), getString(1), getString(2), false, mutableListOf())
            else
                null

        }
    }

    private fun getIngredientById(ingredientId: Long): Ingredient? {
        with(readableDatabase.query("ingredients", arrayOf("id", "name"), "id = ?", arrayOf(ingredientId.toString()), null, null, null)) {
            moveToFirst()

            return if (!isAfterLast)
                Ingredient(getLong(0), getString(1), false)
            else null
        }
    }

    fun saveNewIngredientInStorage(ingredientId: Long) {
        writableDatabase.insert("ingredients_in_storage", null, ContentValues().apply {
            put("ingredient_id", ingredientId)
        })
    }

    fun removeFromStorage(ingredientId: Long) {
        writableDatabase.delete("ingredients_in_storage", "ingredient_id = ?", arrayOf(ingredientId.toString()))
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
            put("name", "Wódka pigwowa")
        })
        db?.insert("ingredients", null, ContentValues().apply {
            put("type", BASE_ALCOHOL.id)
            put("name", "Wódka śliwkowa")
        })
        db?.insert("ingredients", null, ContentValues().apply {
            put("type", BASE_ALCOHOL.id)
            put("name", "Wódka wiśniowa")
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
            put("type", JUICE.id)
            put("name", "Tonik")
        })
        db?.insert("ingredients", null, ContentValues().apply {
            put("type", JUICE.id)
            put("name", "Sok pomarańczowy")
        })
        db?.insert("ingredients", null, ContentValues().apply {
            put("type", ADDITIONAL.id)
            put("name", "Limonka")
        })
        db?.insert("ingredients", null, ContentValues().apply {
            put("type", ADDITIONAL.id)
            put("name", "Cytryna")
        })


        db?.insert("recipes", null, ContentValues().apply {
            put("name", "Cuba Libre")
            put("description", "Do szklanki z lodem wlewam rum, dopełniam colą i wyciskam sok z dwóch ćwiartek limonki, lekko mieszam")
        })
        db?.insert("recipes", null, ContentValues().apply {
            put("name", "Daiquiri")
            put("description", "Do szklanicy wlewam rum i sok z limonki, wstrząsam z lodem i odcedzam do schłodzonego kieliszka")
        })
        db?.insert("recipes", null, ContentValues().apply {
            put("name", "November Rain")
            put("description", "Do szklanki wyciskam sok z cytryny i wrzucam cytrynę do środka. Dodaję wódkę, lód i dopełniam colą")
        })
        db?.insert("recipes", null, ContentValues().apply {
            put("name", "Łowca Androidów")
            put("description", "Do szklanki wlewam wódkę, wyciskam sok z cytryny i wrzucam cytrynę do środka. Dodaję lód i dopełniam tonikiem")
        })
        db?.insert("recipes", null, ContentValues().apply {
            put("name", "Yattaman")
            put("description", "Do szklanki wlewam wódkę, wyciskam sok z cytryny i wrzucam cytrynę do środka. Dodaję lód i dopełniam colą")
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
            put("recipe_id", getRecipeId("Daiquiri", db))
            put("ingredient_id", getIngredientId("Limonka", db))
            put("quantity", 1)
            put("unit", "")
        })
        db?.insert("recipes_ingredients", null, ContentValues().apply {
            put("recipe_id", getRecipeId("November Rain", db))
            put("ingredient_id", getIngredientId("Wódka wiśniowa", db))
            put("quantity", 50)
            put("unit", "ml")
        })
        db?.insert("recipes_ingredients", null, ContentValues().apply {
            put("recipe_id", getRecipeId("November Rain", db))
            put("ingredient_id", getIngredientId("Cytryna", db))
            put("quantity", 0.25)
            put("unit", "")
        })
        db?.insert("recipes_ingredients", null, ContentValues().apply {
            put("recipe_id", getRecipeId("November Rain", db))
            put("ingredient_id", getIngredientId("Cola", db))
            put("quantity", 150)
            put("unit", "ml")
        })
        db?.insert("recipes_ingredients", null, ContentValues().apply {
            put("recipe_id", getRecipeId("Łowca Androidów", db))
            put("ingredient_id", getIngredientId("Wódka śliwkowa", db))
            put("quantity", 70)
            put("unit", "ml")
        })
        db?.insert("recipes_ingredients", null, ContentValues().apply {
            put("recipe_id", getRecipeId("Łowca Androidów", db))
            put("ingredient_id", getIngredientId("Cytryna", db))
            put("quantity", 0.25)
            put("unit", "")
        })
        db?.insert("recipes_ingredients", null, ContentValues().apply {
            put("recipe_id", getRecipeId("Łowca Androidów", db))
            put("ingredient_id", getIngredientId("Tonik", db))
            put("quantity", 150)
            put("unit", "ml")
        })
        db?.insert("recipes_ingredients", null, ContentValues().apply {
            put("recipe_id", getRecipeId("Yattaman", db))
            put("ingredient_id", getIngredientId("Wódka pigwowa", db))
            put("quantity", 50)
            put("unit", "ml")
        })
        db?.insert("recipes_ingredients", null, ContentValues().apply {
            put("recipe_id", getRecipeId("Yattaman", db))
            put("ingredient_id", getIngredientId("Cytryna", db))
            put("quantity", 0.25)
            put("unit", "")
        })
        db?.insert("recipes_ingredients", null, ContentValues().apply {
            put("recipe_id", getRecipeId("Yattaman", db))
            put("ingredient_id", getIngredientId("Cola", db))
            put("quantity", 150)
            put("unit", "ml")
        })
    }

    fun resetDbData() {
        val db = readableDatabase
        db.delete("recipes_ingredients", "", emptyArray())
        db.delete("ingredients_in_storage", "", emptyArray())
        db.delete("recipes", "", emptyArray())
        db.delete("ingredients", "", emptyArray())
        db.delete("ingredient_types", "", emptyArray())

        initData(db)
    }
}