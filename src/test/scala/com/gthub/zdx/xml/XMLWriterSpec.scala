package com.gthub.zdx.xml

import com.github.zdx.xml.XMLWriter
import org.scalatest.{FunSpec, Matchers}

/**
  * Created by zhoudunxiong on 2018/5/6.
  */
class XMLWriterSpec extends FunSpec with Matchers {

  case class User(id: Int, name: String, online: Boolean)

  case class Course(name: String, score: Int)

  case class Student(id: Int, name: String, course: Course)

  describe("XMLWriter test") {
    it("should write User to Elem") {
      val user = User(1001, "zdx", false)
      val userELem =
        <user><id>1001</id><name>zdx</name><online>false</online></user>
      XMLWriter.write(user) should be (userELem)
      val course = Course("math", 100)
      val courseElem =
        <course><name>math</name><score>100</score></course>
      XMLWriter.write(course) should be (courseElem)
      val student = Student(1001, "zdx", course)
      val studentElem =
        <student id="1001" name="zdx">{courseElem}</student>
      XMLWriter.write(student) should be (studentElem)
    }
  }
}
