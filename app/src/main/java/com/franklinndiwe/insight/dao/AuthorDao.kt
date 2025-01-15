package com.franklinndiwe.insight.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.franklinndiwe.insight.data.Author
import com.franklinndiwe.insight.data.Suggestion
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AuthorDao {
    @RawQuery([Author::class])
    protected abstract fun getAuthors(query: SupportSQLiteQuery): PagingSource<Int, Author>

    @Query("SELECT * FROM authors WHERE id = :id")
    abstract fun getAuthorById(id: Int): Flow<Author?>

    @Query("SELECT id FROM authors WHERE LOWER(name) = LOWER(:query)")
    abstract fun getAuthorId(query: String): Flow<Int?>

    @Query("SELECT authors.id, authors.name FROM authors JOIN author_fts ON (authors.id = author_fts.rowid) WHERE author_fts MATCH :query LIMIT 10")
    abstract fun authorSuggestions(query: String): Flow<List<Suggestion>>

    @Query("SELECT COUNT(q.id) FROM authors a LEFT JOIN quotes q ON (a.id = q.authorId) WHERE a.id = :id GROUP BY a.id")
    abstract fun getQuoteCount(id: Int): Flow<Int>

    fun getAllAuthors(
        sortBy: String, order: String,
    ): PagingSource<Int, Author> {
        val statement = "SELECT * FROM authors ${
            orderLogic(
                sortBy, order = order
            )
        }"
        return getAuthors(SimpleSQLiteQuery(statement))
    }

    @Query("SELECT authors.* FROM authors JOIN author_fts ON (authors.id = author_fts.rowid) WHERE author_fts MATCH :query")
    abstract fun searchAuthor(query: String): PagingSource<Int, Author>

    fun getFavoriteAuthors(
        sortBy: String, order: String,
    ): PagingSource<Int, Author> {
        val statement = "SELECT * FROM authors WHERE liked NOT NULL ${
            orderLogic(
                sortBy, order = order
            )
        }"
        return getAuthors(SimpleSQLiteQuery(statement))
    }

    @Query("SELECT * FROM authors WHERE following = 1")
    abstract fun getFollowingAuthors(): PagingSource<Int, Author>

    @Query("SELECT COUNT(id) FROM authors WHERE following = 1")
    abstract fun getFollowingAuthorsCount(): Flow<Int>

    fun getUserAuthors(
        sortBy: String, order: String,
    ): PagingSource<Int, Author> {
        val statement = "SELECT * FROM authors WHERE userGenerated NOT NULL ${
            orderLogic(
                sortBy, order = order
            )
        }"
        return getAuthors(SimpleSQLiteQuery(statement))
    }

    fun getPopularAuthors(sortBy: String, order: String): PagingSource<Int, Author> {
        val statement = "SELECT * FROM authors WHERE popular = 1 ${
            orderLogic(sortBy, order = order)
        }"
        return getAuthors(SimpleSQLiteQuery(statement))
    }

    @Query("SELECT COUNT(*) FROM authors WHERE LOWER(name) = LOWER(:author)")
    abstract fun checkForExistingAuthor(author: String): Int

    @Insert
    abstract suspend fun insert(author: Author): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(authors: List<Author>)

    @Update
    abstract suspend fun update(author: Author)

    @Delete
    abstract suspend fun delete(vararg author: Author)
}