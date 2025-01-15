package com.franklinndiwe.insight.utils

import java.io.EOFException
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream


/**
 * Reads a TrueType font file into a byte array and provides file like functions for array access.
 */
class FontFileReader {
    /**
     * Returns the size of the file.
     *
     * @return int The filesize
     */
    private var fileSize = 0 // file size

    /**
     * Returns current file position.
     *
     * @return int The current position.
     */
    var currentPos = 0 // current position in file
        private set

    /**
     * Returns the full byte array representation of the file.
     *
     * @return byte array.
     */
    private lateinit var allBytes: ByteArray

    /**
     * Constructor
     *
     * @param in
     * InputStream to read from
     * @throws IOException
     * In case of an I/O problem
     */
    constructor(`in`: InputStream) {
        init(`in`)
    }

    /**
     * Constructor
     *
     * @param fileName
     * filename to read
     * @throws IOException
     * In case of an I/O problem
     */
    constructor(fileName: String?) {
        val f = fileName?.let { File(it) }
        val `in`: InputStream = FileInputStream(f)
        `in`.use { init(it) }
    }

    /**
     * Initializes class and reads stream. Init does not close stream.
     *
     * @param in
     * InputStream to read from new array with size + inc
     * @throws IOException
     * In case of an I/O problem
     */
    @Throws(IOException::class)
    private fun init(`in`: InputStream) {
        allBytes = IOUtils.toByteArray(`in`)
        fileSize = allBytes.size
        currentPos = 0
    }

    /**
     * Read 1 byte.
     *
     * @return One byte
     * @throws IOException
     * If EOF is reached
     */
    @Throws(IOException::class)
    private fun read(): Byte {
        if (currentPos >= fileSize) {
            throw EOFException("Reached EOF, file size=$fileSize")
        }
        return allBytes[currentPos++]
    }

    /**
     * Read 1 signed byte.
     *
     * @return One byte
     * @throws IOException
     * If EOF is reached
     */
    @Throws(IOException::class)
    fun readTTFByte(): Byte {
        return read()
    }

    /**
     * Read 4 bytes.
     *
     * @return One signed integer
     * @throws IOException
     * If EOF is reached
     */
    @Throws(IOException::class)
    fun readTTFLong(): Int {
        var ret = readTTFUByte().toLong() // << 8;
        ret = (ret shl 8) + readTTFUByte()
        ret = (ret shl 8) + readTTFUByte()
        ret = (ret shl 8) + readTTFUByte()
        return ret.toInt()
    }

    /**
     * Read an ISO-8859-1 string of len bytes.
     *
     * @param len
     * The bytesToUpload of the string to read
     * @return A String
     * @throws IOException
     * If EOF is reached
     */
    @Throws(IOException::class)
    fun readTTFString(len: Int): String {
        if (len + currentPos > fileSize) {
            throw EOFException("Reached EOF, file size=$fileSize")
        }
        val tmp = ByteArray(len)
        System.arraycopy(allBytes, currentPos, tmp, 0, len)
        currentPos += len
        val encoding: String = if (tmp.isNotEmpty() && tmp[0].toInt() == 0) {
            "UTF-16BE"
        } else {
            "ISO-8859-1"
        }
        return String(tmp, charset(encoding))
    }

    /**
     * Read an ISO-8859-1 string of len bytes.
     *
     * @param len
     * The bytesToUpload of the string to read
     * @param encodingID
     * the string encoding id (presently ignored; always uses UTF-16BE)
     * @return A String
     * @throws IOException
     * If EOF is reached
     */
    @Throws(IOException::class)
    fun readTTFString(len: Int, encodingID: Int): String {
        if (len + currentPos > fileSize) {
            throw EOFException("Reached EOF, file size=$fileSize")
        }
        val tmp = ByteArray(len)
        System.arraycopy(allBytes, currentPos, tmp, 0, len)
        currentPos += len
        val encoding = "UTF-16BE" // Use this for all known encoding IDs for now
        return String(tmp, charset(encoding))
    }

    /**
     * Read 1 unsigned byte.
     *
     * @return One unsigned byte
     * @throws IOException
     * If EOF is reached
     */
    @Throws(IOException::class)
    fun readTTFUByte(): Int {
        val buf = read()
        return if (buf < 0) {
            256 + buf
        } else {
            buf.toInt()
        }
    }

    /**
     * Read 4 bytes.
     *
     * @return One unsigned integer
     * @throws IOException
     * If EOF is reached
     */
    @Throws(IOException::class)
    fun readTTFULong(): Long {
        var ret = readTTFUByte().toLong()
        ret = (ret shl 8) + readTTFUByte()
        ret = (ret shl 8) + readTTFUByte()
        ret = (ret shl 8) + readTTFUByte()
        return ret
    }

    /**
     * Read 2 bytes unsigned.
     *
     * @return One unsigned short
     * @throws IOException
     * If EOF is reached
     */
    @Throws(IOException::class)
    fun readTTFUShort(): Int {
        return (readTTFUByte() shl 8) + readTTFUByte()
    }

    /**
     * Set current file position to offset
     *
     * @param offset
     * The new offset to set
     * @throws IOException
     * In case of an I/O problem
     */
    @Throws(IOException::class)
    fun seekSet(offset: Long) {
        if (offset > fileSize || offset < 0) {
            throw EOFException("Reached EOF, file size=$fileSize offset=$offset")
        }
        currentPos = offset.toInt()
    }

    /**
     * Skip a given number of bytes.
     *
     * @param add
     * The number of bytes to advance
     * @throws IOException
     * In case of an I/O problem
     */
    @Throws(IOException::class)
    fun skip(add: Long) {
        seekSet(currentPos + add)
    }

    companion object {

        /**
         * Read a font file
         *
         * @param inputStream InputStream to read from
         * @return
         * @throws IOException if an error occurred while reading the font.
         */
        @Throws(IOException::class)
        fun readTTF(inputStream: InputStream): TTFFile {
            val ttfFile = TTFFile()
            ttfFile.readFont(FontFileReader(inputStream))
            return ttfFile
        }
    }
}