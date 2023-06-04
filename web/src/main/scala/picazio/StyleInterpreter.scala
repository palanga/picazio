package picazio

import com.raquo.laminar.api.L.*
import com.raquo.laminar.modifiers.KeySetter.StyleSetter
import com.raquo.laminar.nodes.*
import org.scalajs.dom.Element
import picazio.style.*
import zio.*

import scala.util.chaining.scalaUtilChainingOps

private[picazio] class StyleInterpreter(implicit runtime: Runtime[Theme], unsafe: Unsafe) {

  private[picazio] def applyStyles(shape: Shape)(element: ReactiveElement[Element]): ReactiveElement[Element] =
    (defaultStylesForShape(shape) ++ shape.customStyles)
      .pipe(applyTheme)
      .pipe(convertToLaminarStyleSetters)
      .pipe(amendStyles(element))

  private def defaultStylesForShape(shape: Shape): Styles =
    shape match {
      case _: Shape.Text                 => TextStyles.default
      case _: Shape.StaticText           => TextStyles.default
      case _: Shape.TextInput            => InputTextStyles.default
      case _: Shape.SubscribedTextInput  => InputTextStyles.default
      case _: Shape.SignaledTextInput    => InputTextStyles.default
      case _: Shape.Button               => ButtonStyles.default
      case _: Shape.StaticColumn         => Styles.empty
      case _: Shape.DynamicColumn        => Styles.empty
      case _: Shape.StaticRow            => Styles.empty
      case _: Shape.DynamicRow           => Styles.empty
      case Shape.OnClick(_, inner)       => defaultStylesForShape(inner) ++ OnClickStyles.default
      case Shape.OnInput(_, inner)       => defaultStylesForShape(inner)
      case Shape.OnInputFilter(_, inner) => defaultStylesForShape(inner)
    }

  private def applyTheme(styles: Styles): ThemedStyles =
    new ThemedStyles(styles, runtime.environment.get[Theme])

  private def convertToLaminarStyleSetters(themedStyles: ThemedStyles): Seq[StyleSetter] =
    themedStyles.styles.self.map {
      case Style.MarginTop(spacing)     => marginTop     := (spacing * themedStyles.theme.spacingMultiplier).toString
      case Style.PaddingTop(spacing)    => paddingTop    := (spacing * themedStyles.theme.spacingMultiplier).toString
      case Style.PaddingBottom(spacing) => paddingBottom := (spacing * themedStyles.theme.spacingMultiplier).toString
      case Style.Cursor(c)              => cursor        := c.toString.toLowerCase
    }

  private def amendStyles(element: ReactiveElement[Element])(styleSetters: Seq[StyleSetter]): ReactiveElement[Element] =
    element match {
      case html: ReactiveHtmlElement[?] => html.amend(styleSetters)
      case svg: ReactiveSvgElement[?]   => svg
      case _ =>
        throw new IllegalArgumentException(
          s"ReactiveHtmlElement or ReactiveSvgElement expected. Got: ${element.getClass.getName}"
        )
    }

}
