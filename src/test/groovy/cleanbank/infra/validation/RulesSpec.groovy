package cleanbank.infra.validation

import org.apache.commons.lang3.StringUtils
import spock.lang.Specification
import spock.lang.Subject

class RulesSpec extends Specification {

  static record Bean(String name, String country) {}

  @Subject
  def rules = new Rules()

  def "throws violations in rule declaration order"() {
    when:
    def bean = new Bean(name, country)
    rules
      .with(bean::name, StringUtils::isNotEmpty, "Name is empty")
      .with(bean::country, StringUtils::isNotEmpty, "Country is empty", nested ->
        nested.with(bean::country, StringUtils::isAllUpperCase, "Country '%s' must be uppercase"))
      .enforce(bean)

    then:
    def e = thrown(Rules.Violations)
    e.violations() == violations

    where:
    name | country || violations
    ""   | ""      || ["Name is empty", "Country is empty"]
    ""   | "US"    || ["Name is empty"]
    "Ed" | ""      || ["Country is empty"]
    "Ed" | "us"    || ["Country 'us' must be uppercase"]
  }

  def "stays silent if all rules pass"() {
    when:
    rules
      .with(String::toString, StringUtils::isNotEmpty, "This will always pass")
      .enforce("not empty string")

    then:
    noExceptionThrown()
  }


}