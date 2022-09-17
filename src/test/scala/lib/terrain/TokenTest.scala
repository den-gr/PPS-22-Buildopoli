package lib.terrain

import org.scalatest.funsuite.AnyFunSuite
import lib.terrain.Token
import org.scalatest.BeforeAndAfterEach

class TokenTest extends AnyFunSuite with BeforeAndAfterEach:

  val l1: String = "house"
  val l2: String = "hotel"
  var token: Token = _

  override def beforeEach(): Unit =
    token = Token(Seq(l1, l2), Seq(4, 1), Seq(Seq(10, 20, 30, 40), Seq(200)), Seq(10, 20))

  test("A token where the array size is not coherent is not valid") {
    assertThrows[Exception](
      Token(Seq(l1, l2), Seq(4, 1, 3), Seq(Seq(250, 500, 1125, 375), Seq(500)), Seq(25, 50))
    )
    assertThrows[Exception](
      Token(Seq(l1, l2), Seq(4, 1), Seq(Seq(250, 500, 1125, 375)), Seq(25, 50))
    )
  }

  test("A token where the number of bonus is not the same as the max is not valid") {
    assertThrows[Exception](
      Token(Seq(l1, l2), Seq(4, 1), Seq(Seq(250, 500, 1125), Seq(500)), Seq(25, 50))
    )
  }

  test("At the beginning we have zero tokens") {
    assert(token.getNumToken(l1) == 0)
    assert(token.getNumToken(l2) == 0)
  }

  test("It is possible to add token of the next level only when the previous level is full") {
    assertThrows[Exception](token.addToken(l2, 1))
    token = token.addToken(l1, 4)

    assert(token.getNumToken(l1) == 4)
    token = token.addToken(l2, 1)
    assert(token.getNumToken(l2) == 1)
    assert(token.getNumToken(l1) == 0)
  }

  test("It is possible to get the listing of token you can build") {
    assert(token.listAvailableToken() == Seq(l1))
    token = token.addToken(l1, 4)
    assert(token.listAvailableToken() == Seq(l2))
    token = token.addToken(l2, 1)
    assert(token.listAvailableToken() == Nil)
  }
