package core.generator

import org.scalatest.{ ShouldMatchers, FunSpec }

class GeneratorUtilSpec extends FunSpec with ShouldMatchers {

  it("isJsonDocumentMethod") {
    GeneratorUtil.isJsonDocumentMethod("GET") should be(false)
    GeneratorUtil.isJsonDocumentMethod("POST") should be(true)
    GeneratorUtil.isJsonDocumentMethod("PUT") should be(true)
    GeneratorUtil.isJsonDocumentMethod("PATCH") should be(true)
  }

  it("namedParametersInPath") {
    GeneratorUtil.namedParametersInPath("/users") should be(Seq.empty)
    GeneratorUtil.namedParametersInPath("/users/:guid") should be(Seq("guid"))
    GeneratorUtil.namedParametersInPath("/:org/docs/:version") should be(Seq("org", "version"))
    GeneratorUtil.namedParametersInPath("/:org/:service/:version") should be(Seq("org", "service", "version"))
  }

  it("formatComment") {
    GeneratorUtil.formatComment("test") should be("# test")
    GeneratorUtil.formatComment("test this") should be("# test this")

    //val source = "Search all users. Results are always paginated. You must specify at least 1 parameter - either a guid, email or token."
    //val target = "Search all users. Results are always paginated. You must specify at least 1\nparameter - either a guid, email or token."

    val source = "Search all users. Results are always paginated. You must specify at least 1 parameter"
    val target = """
# Search all users. Results are always paginated. You must specify at least 1
# parameter
""".trim
    GeneratorUtil.formatComment(source) should be(target)
  }


}
