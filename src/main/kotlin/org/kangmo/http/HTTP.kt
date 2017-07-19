package org.kangmo.http
/*
    HTTP.scala ; A helper for sending HTTP requests.
*/
import kotlinx.coroutines.experimental.runBlocking
import java.net.URL
import java.io.DataOutputStream
import java.nio.charset.Charset
import java.util.*
import javax.net.ssl.HttpsURLConnection

object HTTP {
    fun post(urlStr:String, postData:String = "", httpHeaders : Map<String,String> = mapOf() ) : String {
      val url = URL(urlStr);
      val con = url.openConnection();

      con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

      // Add http headers if any
      httpHeaders.map { (header, value) ->
        con.setRequestProperty(header, value)
      }


      // Send post request
      con.setDoOutput(true);
      val wr = DataOutputStream(con.getOutputStream())
      wr.write(postData.toByteArray(Charset.forName("UTF-8")) )
      wr.flush()
      wr.close()

      val input = con.getInputStream();

      val response = input.bufferedReader().use { it.readText() }

      input.close()

      return response
    }

    fun get(urlStr:String, httpHeaders : Map<String,String> = mapOf() ) : String {
      val url = URL(urlStr);
      val con = url.openConnection();

      con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

      // Add http headers if any
      httpHeaders.map { (header, value) ->
        con.setRequestProperty(header, value);
      }

      val input = con.getInputStream();

      val response = input.bufferedReader().use { it.readText() }

      input.close()

      return response
    }
}