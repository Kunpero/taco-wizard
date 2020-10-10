package rs.kunpero.fatpak.config.filter

import org.apache.commons.io.IOUtils
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

class AuthenticationRequestWrapper(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
    private var cachedBytes: ByteArrayOutputStream? = null

    override fun getInputStream(): ServletInputStream? {
        if (cachedBytes == null) cacheInputStream()
        return CachedServletInputStream()
    }

    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(inputStream))
    }

    private fun cacheInputStream() {
        /* Cache the inputstream in order to read it multiple times. For
         * convenience, I use apache.commons IOUtils
         */
        cachedBytes = ByteArrayOutputStream()
        IOUtils.copy(super.getInputStream(), cachedBytes)
    }

    /* An inputstream which reads the cached request body */
    inner class CachedServletInputStream : ServletInputStream() {
        private val input: ByteArrayInputStream = ByteArrayInputStream(cachedBytes?.toByteArray())

        override fun isFinished(): Boolean {
            return true
        }

        override fun isReady(): Boolean {
            return true
        }

        override fun setReadListener(readListener: ReadListener) {
            throw RuntimeException("FAIL")
        }

        override fun read(): Int {
            return input.read()
        }
    }
}