package com.github.zdx.xml

import shapeless.{HNil, HList, ::, Generic}

import scala.xml.Node

/**
  * Created by zhoudunxiong on 2018/5/6.
  */
object XMLWriter {

  def getFields(a: AnyRef): Map[String, Any] = {
    a.getClass.getDeclaredFields.foldLeft(Map[String, Any]()){ (m, f) =>
      f.setAccessible(true)
      if (f.getName != "$outer")
        m + (f.getName -> f.get(a))
      else
        m
    }
  }

  def write(a: AnyRef): Node = {
    def loop(a: AnyRef): String = {
      val name = a.getClass.getSimpleName.toLowerCase
      val fields = getFields(a)
      val (attrs, elems) = fields.partition {
        case (_, v) => v.isInstanceOf[Int] || v.isInstanceOf[String] || v.isInstanceOf[Boolean]
      }
      if (elems.isEmpty)
        attrs.map{ case (k, v) => s"<$k>$v</$k>" }.mkString(s"<$name>", "", s"</$name>")
      else {
        val attrStr = attrs.map{ case(k, v) => s""" $k="$v" """}.mkString
        val elemStr = elems.map{ case(_, v) => loop(v.asInstanceOf[AnyRef])}.mkString
        s"<$name $attrStr>$elemStr</$name>"
      }
    }
    val test = loop(a)
    scala.xml.XML.loadString(test)
  }


}