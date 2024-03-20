package picazio

import org.scalatest.matchers.should.Matchers
import picazio.test.*

class TextTest extends WebInterpreterSpec with Matchers {

  testShape("a long text should wrap by default") { render =>
    render(
      Shape.text(
        """
          |“Cuando haya desaparecido la subordinación esclavizadora de los individuos a la división del trabajo, y con
          | ella, la oposición entre el trabajo intelectual y el trabajo manual; cuando el trabajo no sea solamente un
          | medio de vida, sino la primera necesidad vital; cuando, con el desarrollo de los individuos en todos sus
          | aspectos, crezcan también las fuerzas productivas y corran a chorro lleno los manantiales de la riqueza
          | colectiva, sólo entonces podrá rebasarse totalmente el estrecho horizonte del derecho burgués, y la
          | sociedad podrá escribir en sus banderas: ¡De cada cual, según sus capacidades; a cada cual, según sus
          | necesidades!“
          |""".stripMargin
      )
    )
      .map { root =>
        root.styles should contain theSameElementsAs textDefaultStyles
      }
  }

  testShape("a long text can be set not to wrap ") { render =>
    render(
      Shape.column(
        Shape.text(
          """
            |"La conquista del poder cultural es previa a la del poder político, y esto se logra mediante la acción
            |concertada de los intelectuales llamados 'orgánicos' infiltrados en todos los medios de comunicación,
            |expresión y universitarios”
            |""".stripMargin
        ).noWrap
      )
    )
      .map { root =>
        root.head.styles should contain theSameElementsAs (textDefaultStyles + ("white-space" -> "nowrap"))
      }
  }

  testShape("a long text can be hidden by ellipsis ") { render =>
    render(
      Shape.column(
        Shape.text(
          """
            |«En una sociedad formada por el poder del dinero, en una sociedad en que las masas productoras vegetan en
            |la miseria, mientras unos pocos adinerados sólo acierta a ser parásitos, no puede haber libertad real y
            |verdadera»
            |""".stripMargin
        ).overflowEllipsis
      )
    )
      .map { root =>
        root.head.styles should contain theSameElementsAs textDefaultStyles ++ Set(
          "text-overflow" -> "ellipsis",
          "white-space"   -> "nowrap",
          "overflow"      -> "hidden",
        )
      }
  }

  private val textDefaultStyles = RenderedStyleSet(
    "font-family" -> "system-ui",
    "font-size"   -> "16px",
    "padding-top" -> "4px",
  )

}
