package org.kangmo.http
/*
    HTTP.scala ; A helper for sending HTTP requests.
*/
import java.io._
import java.net.URL
import play.api.mvc._
import javax.net.ssl.HttpsURLConnection

object HTTP {
    def post(urlStr:String, postData:String = "", httpHeaders : Map[String,String] = collection.immutable.HashMap() ) = {
      val url = new URL(urlStr);
      val con = url.openConnection();

      con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

      // Add http headers if any
      httpHeaders.map { case (header, value) =>
        con.setRequestProperty(header, value);
      }

      // Send post request
      con.setDoOutput(true);
      val wr = new DataOutputStream(con.getOutputStream());
      wr.write(postData.getBytes("UTF-8"));
      wr.flush();
      wr.close();

      val in = con.getInputStream();

      val response = io.Source.fromInputStream(in, "UTF-8").mkString("")

      in.close();

      response
    }

    def get(urlStr:String, httpHeaders : Map[String,String] = collection.immutable.HashMap() ) = {
      val url = new URL(urlStr);
      val con = url.openConnection();

      con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
      con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

      // Add http headers if any
      httpHeaders.map { case (header, value) =>
        con.setRequestProperty(header, value);
      }

      val in = con.getInputStream();

      val response = io.Source.fromInputStream(in, "UTF-8").mkString("")

      in.close();

      response
    }        
}