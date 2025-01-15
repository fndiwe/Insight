package com.franklinndiwe.insight.utils

/**
 * Represents table names as found in a TrueType font's Table Directory. TrueType fonts may have
 * custom tables so we cannot use an enum.
 */
class TTFTableName private constructor(
    /**
     * Returns the name of the table as it should be in the Directory Table.
     */
    val name: String,
) {

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        if (other !is TTFTableName) {
            return false
        }
        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return name
    }

    companion object {
        /** The first table in a TrueType font file containing metadata about other tables.  */
        val TABLE_DIRECTORY = TTFTableName("tableDirectory")

        /** Naming table.  */
        val NAME = TTFTableName("name")

        /**
         * Returns an instance of this class corresponding to the given string representation.
         *
         * @param tableName
         * table name as in the Table Directory
         * @return TTFTableName
         */
        fun getValue(tableName: String?): TTFTableName {
            if (tableName != null) {
                return TTFTableName(tableName)
            }
            throw IllegalArgumentException("A TrueType font table name must not be null")
        }
    }
}