package com.franklinndiwe.insight.utils

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


object IOUtils {
    private const val DEFAULT_BUFFER = 4096 // 4kb
    private const val EOF = -1 // end of file

    /**
     * Get the contents of an `InputStream` as a `byte[]`.
     *
     *
     * This method buffers the input internally, so there is no need to use a `BufferedInputStream`.
     *
     * @param input
     * the `InputStream` to read from
     * @return the requested byte array
     * @throws NullPointerException
     * if the input is null
     * @throws IOException
     * if an I/O error occurs
     */
    @Throws(IOException::class)
    fun toByteArray(input: InputStream): ByteArray {
        val output = ByteArrayOutputStream()
        copy(input, output)
        return output.toByteArray()
    }

    /**
     * Copy bytes from an `InputStream` to an `OutputStream`.
     *
     *
     * This method buffers the input internally, so there is no need to use a `BufferedInputStream`.
     *
     *
     * Large streams (over 2GB) will return a bytes copied value of `-1` after the copy has
     * completed since the correct number of bytes cannot be returned as an int. For large streams
     * use the `copyLarge(InputStream, OutputStream)` method.
     *
     * @param input
     * the `InputStream` to read from
     * @param output
     * the `OutputStream` to write to
     * @return the number of bytes copied, or -1 if &gt; Integer.MAX_VALUE
     * @throws NullPointerException
     * if the input or output is null
     * @throws IOException
     * if an I/O error occurs
     */
    @Throws(IOException::class)
    fun copy(input: InputStream, output: OutputStream): Int {
        val count = copyLarge(input, output)
        return if (count > Int.MAX_VALUE) {
            -1
        } else count.toInt()
    }
    /**
     * Copy bytes from a large (over 2GB) `InputStream` to an `OutputStream`.
     *
     *
     * This method uses the provided buffer, so there is no need to use a `BufferedInputStream`.
     *
     * @param input
     * the `InputStream` to read from
     * @param output
     * the `OutputStream` to write to
     * @param buffer
     * the buffer to use for the copy
     * @return the number of bytes copied
     * @throws NullPointerException
     * if the input or output is null
     * @throws IOException
     * if an I/O error occurs
     */
    /**
     * Copy bytes from a large (over 2GB) `InputStream` to an `OutputStream`.
     *
     *
     * This method buffers the input internally, so there is no need to use a `BufferedInputStream`.
     *
     *
     * The buffer size is given by [.DEFAULT_BUFFER].
     *
     * @param input
     * the `InputStream` to read from
     * @param output
     * the `OutputStream` to write to
     * @return the number of bytes copied
     * @throws NullPointerException
     * if the input or output is null
     * @throws IOException
     * if an I/O error occurs
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun copyLarge(
        input: InputStream, output: OutputStream,
        buffer: ByteArray? = ByteArray(
            DEFAULT_BUFFER
        ),
    ): Long {
        var count: Long = 0
        var n: Int
        while (EOF != input.read(buffer).also { n = it }) {
            output.write(buffer, 0, n)
            count += n.toLong()
        }
        return count
    }
}