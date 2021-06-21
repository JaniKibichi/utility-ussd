package com.janikibichi.utils

import java.time.{LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter

object InputUtils{
  object time{
    // MONTH
    def month: String = {
      val now = LocalDateTime.now(ZoneId.of("Africa/Nairobi"))
      val dateTime = DateTimeFormatter.ofPattern("MMMM")
      now.format(dateTime)
    }
    // YEAR
    def year: String = {
      val now = LocalDateTime.now(ZoneId.of("Africa/Nairobi"))
      val dateTime = DateTimeFormatter.ofPattern("uuuu")
      now.format(dateTime)
    }
    // AUTHORIZATION TIME
    def authorizationTime: String = {
      // GMT ISO 8601 FORMAT
      LocalDateTime.now(ZoneId.of("Africa/Accra")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
    }
    // START TIME
    def startTime: String = {
      val now = LocalDateTime.now(ZoneId.of("Africa/Nairobi"))
      val dateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd")
      now.format(dateTime)
    }
    // BANK END TIME
    def bankEndTime: String = {
      val now = LocalDateTime.now(ZoneId.of("Africa/Nairobi"))
      val dateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd")
      now.format(dateTime)
    }
    // BANK START TIME
    def bankStartTime: String = {
      val milliSecondsSixMonthsAgo: Long =
        System.currentTimeMillis() - 15552000000L
      val now = java.time.LocalDateTime.ofInstant(
        java.time.Instant.ofEpochMilli(milliSecondsSixMonthsAgo),
        java.time.ZoneId.of("Africa/Nairobi")
      )
      val dateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd")
      now.format(dateTime)
    }
  }

  object phones{
    // CLEAN UP PHONE NUMBER
    def cleanUpPhoneNumber(phoneNumber: String): String = {
      phoneNumber.take(2) match {
        case "07" =>
          //Assume Kenya, Prefix +254
          val prefix = new StringBuilder("+254")
          phoneNumber.drop(1).addString(prefix).toString()
        case "+2" =>
          //Ok
          phoneNumber
        case "25" =>
          //Append "+"
          val sign = new StringBuilder("+")
          phoneNumber.addString(sign).toString()
        case _ =>
          //Assume Kenya, Prefix +254
          val prefix = new StringBuilder("+254")
          phoneNumber.addString(prefix).toString()
      }
    }
    // CLEAN UP PHONE PER COUNTRY
    def cleanUpPhoneByCountry(phone:String,sessionPhone:String):String={
      phone.take(2) match {
        case "07" =>
          //Assume Kenya, Prefix +254
          val prefix = new StringBuilder(sessionPhone.take(4))
          phone.drop(1).addString(prefix).toString()
        case "+2" =>
          //Ok
          phone
        case "25" =>
          //Append "+"
          val sign = new StringBuilder("+")
          phone.addString(sign).toString()
        case _ =>
          //Assume Kenya, Prefix +254
          val prefix = new StringBuilder(sessionPhone.take(4))
          phone.addString(prefix).toString()
      }
    }
  }

  object meters{
    // CLEAN UP METER RESPONSE
    def cleanUpMeterResponse(meterNumber:String):String ={
      meterNumber.take(2).toLowerCase match{
        case "sm" =>
          //Assume whole meter number entered
          meterNumber.toUpperCase

        case "00" =>
          //Assume last 8 digits entered
          val meterNo = "SM16R-03-"+meterNumber
          meterNo.toUpperCase

        case _ =>
          //Assume user ignored 000
          val meterNo = "SM16R-03-000"+meterNumber
          meterNo.toUpperCase
      }
    }
  }

  object ussd{
    // CLEAN UP USSD RESPONSE
    def cleanUpResponse(response: String): String = {
      response.split('*').last
    }
  }

  object sms{
    // CLEAN UP SMS RESPONSE
    def cleanUpSMSResponse(response:String):String ={
      response.split(' ').last
    }
  }

  object primitives{
    // CONVERT STRING TO BOOLEAN OR RETURN FALSE
    def falseOrBoolean(string: String): Boolean = {
      try {
        string.toBoolean
      } catch {
        case e: Exception => false
      }
    }
    // CONVERT STRING TO INT OR RETURN 0
    def zeroOrInt(string: String): Int = {
      try {
        string.toInt
      } catch {
        case e: Exception => 0
      }
    }
    // CONVERT STRING TO FLOAT OR RETURN 0.00
    def zeroOrFloat(string: String): Float = {
      try {
        string.toFloat
      } catch {
        case e: Exception => 0
      }
    }
    // CONVERT STRING TO DOUBLE OR RETURN 0.00
    def zeroOrDouble(string: String): Double = {
      try {
        string.toDouble
      } catch {
        case e: Exception => 0
      }
    }
    // CONVERT STRING TO LONG OR RETURN 0L
    def zeroOrLong(string: String): Long = {
      try {
        string.toLong
      } catch {
        case e: Exception => 0
      }
    }
    // CONVERT NULL TO STRING
    def nullToString(string: Any): String = {
      string match {
        case null =>
          ""

        case _ =>
          ""
      }
    }
  }
}
