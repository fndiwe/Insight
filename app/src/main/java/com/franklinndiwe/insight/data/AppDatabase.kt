package com.franklinndiwe.insight.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.dao.AuthorDao
import com.franklinndiwe.insight.dao.CategoryDao
import com.franklinndiwe.insight.dao.ColorDao
import com.franklinndiwe.insight.dao.FontDao
import com.franklinndiwe.insight.dao.GradientDao
import com.franklinndiwe.insight.dao.QuoteDao
import com.franklinndiwe.insight.dao.QuoteImageCategoryDao
import com.franklinndiwe.insight.dao.QuoteImageDao
import com.franklinndiwe.insight.dao.SettingDao
import com.franklinndiwe.insight.dao.SortOrderDao

@TypeConverters(Converters::class)
@Database(
    entities = [Quote::class, Author::class, Category::class, Setting::class, QuoteImage::class, QuoteImageCategory::class, Gradient::class, SolidColor::class, AppFont::class, SortOrder::class, CategoryFts::class, QuoteFts::class, AuthorFts::class],
    exportSchema = true,
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
    abstract fun categoryDao(): CategoryDao
    abstract fun authorDao(): AuthorDao
    abstract fun settingDao(): SettingDao
    abstract fun quoteImageDao(): QuoteImageDao
    abstract fun quoteImageCategoryDao(): QuoteImageCategoryDao
    abstract fun gradientDao(): GradientDao
    abstract fun colorDao(): ColorDao
    abstract fun fontDao(): FontDao
    abstract fun sortOrderDao(): SortOrderDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context, AppDatabase::class.java, "inspiration"
                ).fallbackToDestructiveMigration().createFromInputStream {
                    context.resources.openRawResource(R.raw.inspiration)
                }.build().also { Instance = it }
            }
        }
    }
}