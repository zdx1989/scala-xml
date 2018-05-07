package com.github.zdx.xml

import scala.util.{Failure, Success, Try}
import scala.xml.{Elem, Node}


/**
  * Created by zhoudunxiong on 2018/5/5.
  */
trait XMLReader[A] {

  def read(node: Node): Try[A]
}

object XMLReader {

  def apply[A](implicit reader: XMLReader[A]): XMLReader[A] = reader

  def createReader[A](f: Node => Try[A]): XMLReader[A] = new XMLReader[A] {
    override def read(node: Node): Try[A] = f(node)
  }

  def tryFunc[A](f: Node => A): Node => Try[A] =
    node => Try(f(node))


  implicit val intXMLReader: XMLReader[Int] = createReader(tryFunc { node =>
    node.text.toInt
  })

  implicit val stringXMLReader: XMLReader[String] = createReader(tryFunc { node =>
    node.text
  })

  implicit val booleanXMLReader: XMLReader[Boolean] = createReader(tryFunc { node =>
    node.text.toBoolean
  })

  implicit def listXMLReader[A](implicit aXMLReader: XMLReader[A]): XMLReader[List[A]] =
    createReader { node =>
      val nodes = node.child.filter(_.isInstanceOf[Elem]).toList
      sequence(nodes.map(aXMLReader.read))
    }

  def sequence[A](la: List[Try[A]]): Try[List[A]] = {
    def loop(n: Int, res: Try[List[A]]): Try[List[A]] = n match {
      case m if m < 0 => res
      case _ => la(n) match {
        case Success(a) => loop(n - 1, res.map(a :: _))
        case Failure(e) => Failure(e)
      }
    }
    loop(la.length - 1, Success(Nil))
  }
}


