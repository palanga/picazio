package picazio

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.*
import org.scalajs.dom.Element
import picazio.Shape.*
import picazio.style.*
import zio.*

import scala.util.chaining.scalaUtilChainingOps

private[picazio] class StyleInterpreter(implicit runtime: Runtime[Theme], unsafe: Unsafe) {

  private[picazio] def applyStyles(shape: Shape)(element: ReactiveElement[Element]): ReactiveElement[Element] =
    defaultStylesForShape(shape)
      .pipe(applyTheme)
      .pipe(convertToLaminarStyleSetters)
      .pipe(amendStyles(element))

  private def defaultStylesForShape(shape: Shape): Styles =
    shape match {
      case _: Text                 => TextStyles.default
      case _: StaticText           => TextStyles.default
      case _: TextInput            => InputTextStyles.default
      case _: SubscribedTextInput  => InputTextStyles.default
      case _: SignaledTextInput    => InputTextStyles.default
      case _: Shape.Button         => ButtonStyles.default
      case _: StaticColumn         => Styles.empty
      case _: DynamicColumn        => Styles.empty
      case _: StaticRow            => Styles.empty
      case _: DynamicRow           => Styles.empty
      case Styled(styles, inner)   => defaultStylesForShape(inner) ++ styles
      case OnClick(_, inner)       => defaultStylesForShape(inner) ++ OnClickStyles.default
      case OnInput(_, inner)       => defaultStylesForShape(inner)
      case OnInputFilter(_, inner) => defaultStylesForShape(inner)
    }

  private def applyTheme(styles: Styles): ThemedStyles =
    new ThemedStyles(styles, runtime.environment.get[Theme])

  private def convertToLaminarStyleSetters(themedStyles: ThemedStyles): Seq[Modifier[ReactiveHtmlElement.Base]] =
    themedStyles.styles.values.map {
      case Style.MarginTop(size)     => marginTop     := (size * themedStyles.theme.sizeMultiplier).toString
      case Style.PaddingTop(size)    => paddingTop    := (size * themedStyles.theme.sizeMultiplier).toString
      case Style.PaddingBottom(size) => paddingBottom := (size * themedStyles.theme.sizeMultiplier).toString
      case Style.Cursor(c)           => cursor        := c.toString.toLowerCase

      case Style.DynamicPaddingTop(size) =>
        paddingTop <-- signal.toLaminarSignal(size.map(_ * themedStyles.theme.sizeMultiplier).map(_.toString))
    }

  private def amendStyles(
    element: ReactiveElement[Element]
  )(styleModifiers: Seq[Modifier[ReactiveHtmlElement.Base]]): ReactiveElement[Element] =
    element match {
      case html: ReactiveHtmlElement[?] => html.amend(styleModifiers)
      case svg: ReactiveSvgElement[?]   => svg
      case _ =>
        throw new IllegalArgumentException(
          s"ReactiveHtmlElement or ReactiveSvgElement expected. Got: ${element.getClass.getName}"
        )
    }

}
