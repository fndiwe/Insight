package com.franklinndiwe.insight.utils

import java.io.IOException


/**
 * Reads a TrueType file or a TrueType Collection. The TrueType spec can be found at the Microsoft.
 * Typography site: http://www.microsoft.com/truetype/
 */
class TTFFile internal constructor() {
    /** The FontFileReader used to read this TrueType font.  */
    private var fontFile: FontFileReader? = null

    /**
     * Table directory
     */
    private var dirTabs: MutableMap<TTFTableName, TTFDirTabEntry?>? = null

    /**
     * Returns the PostScript name of the font.
     *
     * @return String The PostScript name
     */
    private var postScriptName = ""

    /**
     * Returns the full name of the font.
     *
     * @return String The full name
     */
    var fullName = ""
        private set
    private var notice = ""
    private val familyNames: MutableSet<String> = HashSet()

    /**
     * Returns the font sub family name of the font.
     *
     * @return String The sub family name
     */
    private var subFamilyName = ""

    /**
     * Read Table Directory from the current position in the FontFileReader and fill the global
     * HashMap dirTabs with the table name (String) as key and a TTFDirTabEntry as value.
     *
     * @throws IOException
     * in case of an I/O problem
     */
    @Throws(IOException::class)
    private fun readDirTabs() {
        fontFile!!.readTTFLong() // TTF_FIXED_SIZE (4 bytes)
        val ntabs = fontFile!!.readTTFUShort()
        fontFile!!.skip(6) // 3xTTF_USHORT_SIZE
        dirTabs = HashMap()
        val pd = arrayOfNulls<TTFDirTabEntry>(ntabs)
        for (i in 0 until ntabs) {
            pd[i] = TTFDirTabEntry()
            val tableName = pd[i]!!.read(fontFile!!)
            dirTabs!![TTFTableName.getValue(tableName)] = pd[i]
        }
        dirTabs!![TTFTableName.TABLE_DIRECTORY] = TTFDirTabEntry(0L, fontFile!!.currentPos.toLong())
    }

    /**
     * Reads the font using a FontFileReader.
     *
     * @param in
     * The FontFileReader to use
     * @throws IOException
     * In case of an I/O problem
     */
    @Throws(IOException::class)
    fun readFont(`in`: FontFileReader?) {
        fontFile = `in`
        readDirTabs()
        readName()
    }

    /**
     * Read the "name" table.
     *
     * @throws IOException
     * In case of a I/O problem
     */
    @Throws(IOException::class)
    private fun readName() {
        seekTab(fontFile, TTFTableName.NAME, 2)
        var i = fontFile!!.currentPos
        var n = fontFile!!.readTTFUShort()
        val j = fontFile!!.readTTFUShort() + i - 2
        i += 2 * 2
        while (n-- > 0) {
            fontFile!!.seekSet(i.toLong())
            val platformID = fontFile!!.readTTFUShort()
            val encodingID = fontFile!!.readTTFUShort()
            val languageID = fontFile!!.readTTFUShort()
            val k = fontFile!!.readTTFUShort()
            val l = fontFile!!.readTTFUShort()
            if ((platformID == 1 || platformID == 3) && (encodingID == 0 || encodingID == 1)) {
                fontFile!!.seekSet((j + fontFile!!.readTTFUShort()).toLong())
                val txt: String = if (platformID == 3) {
                    fontFile!!.readTTFString(l, encodingID)
                } else {
                    fontFile!!.readTTFString(l)
                }
                when (k) {
                    0 -> if (notice.isEmpty()) {
                        notice = txt
                    }

                    1, 16 -> familyNames.add(txt)
                    2 -> if (subFamilyName.isEmpty()) {
                        subFamilyName = txt
                    }

                    4 -> if (fullName.isEmpty() || platformID == 3 && languageID == 1033) {
                        fullName = txt
                    }

                    6 -> if (postScriptName.isEmpty()) {
                        postScriptName = txt
                    }

                    else -> {}
                }
            }
            i += 6 * 2
        }
    }

    /**
     * Position inputstream to position indicated in the dirtab offset + offset
     *
     * @param in
     * font file reader
     * @param tableName
     * (tag) of table
     * @param offset
     * from start of table
     * @return true if seek succeeded
     * @throws IOException
     * if I/O exception occurs during seek
     */
    @Throws(IOException::class)
    private fun seekTab(`in`: FontFileReader?, tableName: TTFTableName, offset: Long): Boolean {
        val dt = dirTabs!![tableName]
        if (dt == null) {
            return false
        } else {
            `in`!!.seekSet(dt.offset + offset)
        }
        return true
    }
}