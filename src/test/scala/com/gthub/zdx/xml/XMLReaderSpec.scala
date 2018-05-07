package com.gthub.zdx.xml

import com.github.zdx.xml.{XMLFieldReader, XMLReader}
import org.scalatest.{FunSpec, Matchers}
import shapeless._

import scala.util.Success
import scala.xml.Elem

/**
  * Created by zhoudunxiong on 2018/5/5.
  */
class XMLReaderSpec extends FunSpec with Matchers {
   import XMLFieldReader._

  case class User(id: Int, name: String, online: Boolean)

  case class Course(name: String, score: Int)

  case class Student(id: Int, name: String, courses: List[Course])

  describe("XMLReader test") {

    it("should return User from XML") {
      val id = <id>10001</id>
      val name = <name>zdx</name>
      val online = <online>false</online>
      XMLReader[Int].read(id) should be (Success(10001))
      XMLReader[String].read(name) should be (Success("zdx"))
      XMLReader[Boolean].read(online) should be (Success(false))
      val user =
        <user>
          <id>10001</id>
          <name>zdx</name>
          <online>false</online>
        </user>
      val expected = Success(10001 :: "zdx" :: false :: HNil)
      val nodes = user.child.toList.filter(_.isInstanceOf[Elem])
      XMLFieldReader[Int :: String :: Boolean :: HNil].read(nodes) should be (expected)
      XMLReader[User].read(user) should be (Success(User(10001, "zdx", false)))
    }

    it("should return List[User] from XML with Elem") {
      val users =
        <users>
          <user>
            <id>10001</id>
            <name>zdx</name>
            <online>false</online>
          </user>
          <user>
            <id>10002</id>
            <name>ygy</name>
            <online>true</online>
          </user>
        </users>
      val expected = Success(List(
        User(10001, "zdx", false), User(10002, "ygy", true)
      ))
      XMLReader[List[User]].read(users) should be (expected)
    }

    it("should return User from XML with Attribute") {
      val user = <user id="10001" name="zdx" online="false"></user>
      XMLReader[User].read(user) should be (Success(User(10001, "zdx", false)))
    }

    it("should return Student from XML") {
      val courses =
        <courses>
          <course name="Math" score="100"></course>
          <course name="English" score="98"></course>
        </courses>
      val expected = Success(List(
        Course("Math", 100),
        Course("English", 98)
      ))
      XMLReader[List[Course]].read(courses) should be (expected)
     // Generic[Map[String, Any]].to(Map("id" -> 10001, "name" -> "zdx", "online" -> false))
      //Generic[User].to(User(10001, "zdx", false))
      val student =
        <student id="1001" name="zdx">
          <courses>
            <course name="Math" score="100"></course>
            <course name="English" score="98"></course>
          </courses>
        </student>
      XMLReader[Student].read(student) should be (Success(Student(1001, "zdx", expected.get)))
    }
  }
}
