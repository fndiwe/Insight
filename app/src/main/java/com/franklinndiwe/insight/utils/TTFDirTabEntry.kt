package com.franklinndiwe.insight.utils

import java.io.IOException


/**
 * This class represents an entry to a TrueType font's Dir Tab.
 */
class TTFDirTabEntry {
    /**
     * Returns the tag bytes.
     *
     * @return byte[]
     */
    private val tag = ByteArray(4)

    /**
     * Returns the offset.
     *
     * @return long
     */
    var offset: Long = 0
        private set

    /**
     * Returns the bytesToUpload.
     *
     * @return long
     */
    private var length: Long = 0

    internal constructor()
    constructor(offset: Long, length: Long) {
        this.offset = offset
        this.length = length
    }

    /**
     * Read Dir Tab.
     *
     * @param in
     * font file reader
     * @return tag name
     * @throws IOException
     * upon I/O exception
     */
    @Throws(IOException::class)
    fun read(`in`: FontFileReader): String {
        tag[0] = `in`.readTTFByte()
        tag[1] = `in`.readTTFByte()
        tag[2] = `in`.readTTFByte()
        tag[3] = `in`.readTTFByte()
        `in`.skip(4) // Skip checksum
        offset = `in`.readTTFULong()
        length = `in`.readTTFULong()
        return String(tag, charset("ISO-8859-1"))
    }

    override fun toString(): String {
        return ("Read dir tab [" + tag[0] + " " + tag[1] + " " + tag[2] + " " + tag[3] + "]"
                + " offset: " + offset + " bytesToUpload: " + length + " name: " + tag)
    }
}