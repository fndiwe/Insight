package com.franklinndiwe.insight.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.franklinndiwe.insight.data.Quote
import com.franklinndiwe.insight.data.QuoteV2
import kotlinx.coroutines.flow.Flow

internal fun orderLogic(sortBy: String, prefix: String = "", order: String) =
    "ORDER BY $prefix$sortBy $order"

@Dao
abstract class QuoteDao {
    @Transaction
    @Query("SELECT * FROM quotes WHERE id = :id")
    abstract fun getQuoteById(id: Int): Flow<QuoteV2?>

    @Transaction
    @RawQuery([QuoteV2::class])
    protected abstract fun getQuotes(query: SupportSQLiteQuery): PagingSource<Int, QuoteV2>

    @Transaction
    @Query("SELECT q.* FROM quotes q JOIN categories c ON q.categoryId1 = c.id LEFT JOIN categories c2 ON q.categoryId2 = c2.id WHERE (c.daily = 1 OR c2.daily = 1) ORDER BY RANDOM() LIMIT 1")
    abstract fun dailyQuote(): Flow<QuoteV2?>

    fun getAllQuotes(sortBy: String, order: String): PagingSource<Int, QuoteV2> {
        val statement =
            "SELECT q.* FROM quotes q JOIN categories c ON q.categoryId1 = c.id LEFT JOIN categories c2 ON q.categoryId2 = c2.id WHERE (c.unlocked = 1 AND c2.unlocked = 1 OR q.categoryId2 IS NULL) ${
                orderLogic(
                    sortBy, "q.", order
                )
            }"
        return getQuotes(SimpleSQLiteQuery(statement))
    }

    @Transaction
    @Query("SELECT q.id FROM quotes q JOIN categories c1 ON c1.id = q.categoryId1 LEFT JOIN categories c2 ON c2.id = q.categoryId2 JOIN authors a ON a.id = q.authorId WHERE (c1.following = 1 OR c2.following = 1 OR a.following = 1) AND (c1.unlocked = 1 AND c2.unlocked = 1 OR q.categoryId2 IS NULL) AND (q.userGenerated IS NULL) ORDER BY RANDOM() LIMIT 200")
    abstract fun recommendedQuotes(): Flow<List<Int>>

    fun getQuotesForCategory(
        categoryId: Int, sortBy: String, order: String,
    ): PagingSource<Int, QuoteV2> {
        val statement = "SELECT * FROM quotes WHERE categoryId1 = ? OR categoryId2 = ? ${
            orderLogic(
                sortBy, order = order
            )
        }"
        return getQuotes(SimpleSQLiteQuery(statement, Array(2) { categoryId }))
    }

    fun getQuotesForAuthor(
        authorId: Int, sortBy: String, order: String,
    ): PagingSource<Int, QuoteV2> {
        val statement =
            "SELECT q.* FROM quotes q JOIN categories c ON q.categoryId1 = c.id LEFT JOIN categories c2 ON q.categoryId2 = c2.id WHERE q.authorId = $authorId AND (c.unlocked = 1 AND c2.unlocked = 1 OR q.categoryId2 IS NULL) ${
                orderLogic(
                    sortBy, "q.", order
                )
            }"
        return getQuotes(SimpleSQLiteQuery(statement))
    }

    @Transaction
    @Query("SELECT q.* FROM quotes q JOIN categories c1 ON q.categoryId1 = c1.id JOIN quote_fts ON (q.id = quote_fts.rowid) LEFT JOIN categories c2 ON q.categoryId2 = c2.id WHERE quote_fts MATCH :query AND (c1.unlocked = 1 AND c2.unlocked = 1 OR q.categoryId2 IS NULL)")
    abstract fun searchQuotes(query: String): PagingSource<Int, QuoteV2>

    fun getLikedQuotes(
        sortBy: String, order: String,
    ): PagingSource<Int, QuoteV2> {
        val statement = "SELECT * FROM quotes WHERE liked NOT NULL ${
            orderLogic(
                sortBy, order = order
            )
        }"
        return getQuotes(SimpleSQLiteQuery(statement))
    }

    fun getUserQuotes(
        sortBy: String, order: String,
    ): PagingSource<Int, QuoteV2> {
        val statement = "SELECT * FROM quotes WHERE userGenerated NOT NULL ${
            orderLogic(
                sortBy, order = order
            )
        }"
        return getQuotes(SimpleSQLiteQuery(statement))
    }

    @Query("SELECT COUNT(*) FROM quotes WHERE LOWER(text) = LOWER(:quote)")
    abstract fun checkForExistingQuote(quote: String): Flow<Int>

    @Insert
    abstract suspend fun insert(vararg quote: Quote)

    @Insert
    abstract suspend fun insert(quotes: List<Quote>)

    @Update
    abstract suspend fun update(vararg quote: Quote)

    @Delete
    abstract suspend fun delete(vararg quote: Quote)
}