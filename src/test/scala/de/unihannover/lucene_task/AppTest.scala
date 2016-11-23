package de.unihannover.lucene_task

import org.scalatest.FunSuite

class AppTest extends FunSuite {

  test("index has correct number of documents") {
    assert(App.numDocs == 55927)
  }

  test("search gives correct number of hits") {
    assert(App.search("america@ 2011-2013") == 8387)
    assert(App.search("china@ 2011-2013") == 5227)
    assert(App.search("republic of ireland@ 2011-2011") == 1426)
    assert(App.search("diabetes in young children@ 2011-2012") == 18127)
    assert(App.search("diabetes in young children@ 2011-2009") == 0)
  }

}
