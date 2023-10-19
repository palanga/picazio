package picazio

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.*
import com.raquo.laminar.nodes.ReactiveHtmlElement.Base
import org.scalajs.dom.Element
import picazio.Shape.*
import picazio.style.*
import zio.*

import scala.util.chaining.scalaUtilChainingOps

private[picazio] class StyleInterpreter(implicit runtime: Runtime[Theme], unsafe: Unsafe) {

  private val theme: Theme = runtime.environment.get[Theme]

  def applyTypography(shape: Shape)(styleModifiers: Seq[Modifier[Base]]): Seq[Modifier[Base]] =
    shape match {
      case _: Text                 => (fontFamily := theme.typography) +: styleModifiers
      case _: StaticText           => (fontFamily := theme.typography) +: styleModifiers
      case _: TextInput            => (fontFamily := theme.typography) +: styleModifiers
      case _: SubscribedTextInput  => (fontFamily := theme.typography) +: styleModifiers
      case _: SignaledTextInput    => (fontFamily := theme.typography) +: styleModifiers
      case _: Shape.Button         => (fontFamily := theme.typography) +: styleModifiers
      case Styled(_, inner)        => applyTypography(inner)(styleModifiers)
      case OnClick(_, inner)       => applyTypography(inner)(styleModifiers)
      case OnInput(_, inner)       => applyTypography(inner)(styleModifiers)
      case OnInputFilter(_, inner) => applyTypography(inner)(styleModifiers)
      case _                       => styleModifiers
    }

  private[picazio] def applyStyles(shape: Shape)(element: ReactiveElement[Element]): ReactiveElement[Element] =
    defaultStylesForShape(shape)
      .pipe(applyTheme)
      .pipe(convertToLaminarStyleSetters)
      .pipe(applyTypography(shape))
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
      case _: StreamColumn         => Styles.empty
      case _: StaticRow            => Styles.empty
      case _: DynamicRow           => Styles.empty
      case Styled(styles, inner)   => defaultStylesForShape(inner) ++ styles
      case OnClick(_, inner)       => defaultStylesForShape(inner) ++ OnClickStyles.default
      case OnInput(_, inner)       => defaultStylesForShape(inner)
      case OnInputFilter(_, inner) => defaultStylesForShape(inner)
      case _                       => Styles.empty
    }

  private def applyTheme(styles: Styles): ThemedStyles = new ThemedStyles(styles, theme)

  private def convertToLaminarStyleSetters(themedStyles: ThemedStyles): Seq[Modifier[Base]] =
    themedStyles.styles.values.collect {
      case Style.MarginTop(size)     => marginTop     := (size * themedStyles.theme.sizeMultiplier).toString
      case Style.PaddingTop(size)    => paddingTop    := (size * themedStyles.theme.sizeMultiplier).toString
      case Style.PaddingBottom(size) => paddingBottom := (size * themedStyles.theme.sizeMultiplier).toString
      case Style.Cursor(c)           => cursor        := c.toString.toLowerCase
      case Style.FontSize(size)      => fontSize      := (size * themedStyles.theme.sizeMultiplier * 4).toString + "px"

      case Style.DynamicPaddingTop(size) =>
        paddingTop <-- signal.toLaminarSignal(size.map(_ * themedStyles.theme.sizeMultiplier).map(_.toString))
    }

  private def amendStyles(
    element: ReactiveElement[Element]
  )(styleModifiers: Seq[Modifier[Base]]): ReactiveElement[Element] =
    element match {
      case html: ReactiveHtmlElement[?] => html.amend(styleModifiers)
      case svg: ReactiveSvgElement[?]   => svg
      case _                            =>
        throw new IllegalArgumentException(
          s"ReactiveHtmlElement or ReactiveSvgElement expected. Got: ${element.getClass.getName}"
        )
    }

}
