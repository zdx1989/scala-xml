package com.github.zdx.xml

import scala.util.Try
import scala.xml.{Elem, Node}

/**
  * Created by zhoudunxiong on 2018/5/5.
  */
trait XMLFieldReader[A] {

  def read(nodes: List[Node]): Try[A]
}

object XMLFieldReader {

  def apply[A](implicit reader: XMLFieldReader[A]): XMLFieldReader[A] = reader

  def createReader[A](f: List[Node] => Try[A]): XMLFieldReader[A] = new XMLFieldReader[A] {
    override def read(nodes: List[Node]): Try[A] = f(nodes)
  }

  def tryFunc[A](f: List[Node] => A): List[Node] => Try[A] =
    nodes => Try(f(nodes))

  import shapeless.{HNil, HList, ::, Generic}

  implicit val hNilXMLReader: XMLFieldReader[HNil] = createReader(tryFunc { nodes =>
    if (nodes.isEmpty) HNil
    else throw new IllegalArgumentException(s"node cannot be converted to HNil")
  })

  implicit def hListXMLReader[H, T <: HList](implicit
                                             hXMLReader: XMLReader[H],
                                             tXMLReader: XMLFieldReader[T]): XMLFieldReader[H :: T] =
    createReader{ nodes =>
      for {
        h <- hXMLReader.read(nodes.head)
        t <- tXMLReader.read(nodes.tail)
      } yield h :: t
    }

  implicit def genericXMLReader[A, R](implicit
                                      gen: Generic.Aux[A, R],
                                      rXMLReader: XMLFieldReader[R]): XMLReader[A] =
    XMLReader.createReader { node =>
      val attrs =
        for (attr <- node.attributes.toList)
          yield <attr>{attr.value.text}</attr>
      val elems = node.child.filter(_.isInstanceOf[Elem]).toList
      val nodes = attrs ++ elems
      rXMLReader.read(nodes).map(gen.from)
    }
}
