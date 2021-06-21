package com.janikibichi.utils

import java.net.URLEncoder
import java.nio.charset.{Charset, StandardCharsets}

import com.typesafe.config.ConfigFactory
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Base64

object UtilityUSSDConfig{
  private val config = ConfigFactory.load()

  final case class Credentials(number:Int,secret:String)

  object http{
    val host:String=config.getString("http.host")
    val port:Int = config.getInt("http.port")
  }

  object language{
    def default(phoneNumber:String):String={
      phoneNumber.take(4) match {
        case "+254" =>
          "SW"

        case "+250" =>
          "SW"

        case _ =>
          "EN"
      }
    }
  }
}
