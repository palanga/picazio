package picazio

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.*
import com.raquo.laminar.nodes.ReactiveHtmlElement.Base
import org.scalajs.dom.Element
import picazio.Shape.*
import picazio.style.*
import picazio.style.Style.*
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

  private def defaultStylesForShape(shape: Shape): StyleSheet =
    shape match {
      case _: Text                 => TextStyles.default
      case _: StaticText           => TextStyles.default
      case _: TextInput            => InputTextStyles.default
      case _: SubscribedTextInput  => InputTextStyles.default
      case _: SignaledTextInput    => InputTextStyles.default
      case _: Shape.Button         => ButtonStyles.default
      case _: StaticColumn         => StyleSheet.empty
      case _: DynamicColumn        => StyleSheet.empty
      case _: StreamColumn         => StyleSheet.empty
      case _: StaticRow            => StyleSheet.empty
      case _: DynamicRow           => StyleSheet.empty
      case Styled(styles, inner)   => defaultStylesForShape(inner) ++ styles
      case OnClick(_, inner)       => defaultStylesForShape(inner) ++ OnClickStyles.default
      case OnInput(_, inner)       => defaultStylesForShape(inner)
      case OnInputFilter(_, inner) => defaultStylesForShape(inner)
      case _                       => StyleSheet.empty
    }

  private def applyTheme(styles: StyleSheet): ThemedStyles = ThemedStyles(styles, theme)

  private def convertToLaminarStyleSetters(themedStyles: ThemedStyles): Seq[Modifier[Base]] = {
    val ThemedStyles(styles, Theme(sizeMultiplier, _)) = themedStyles
    styles.values.map {
      case MarginTop(size) => marginTop := (size * sizeMultiplier).toString + "px"

      case PaddingTop(size)    => paddingTop    := (size * sizeMultiplier).toString + "px"
      case PaddingBottom(size) => paddingBottom := (size * sizeMultiplier).toString + "px"
      case PaddingStart(size)  => paddingLeft   := (size * sizeMultiplier).toString + "px"
      case PaddingEnd(size)    => paddingRight  := (size * sizeMultiplier).toString + "px"

      case BorderTopWidth(size: Size)    => borderTopWidth    := (size * sizeMultiplier).toString + "px"
      case BorderBottomWidth(size: Size) => borderBottomWidth := (size * sizeMultiplier).toString + "px"
      case BorderStartWidth(size: Size)  => borderLeftWidth   := (size * sizeMultiplier).toString + "px"
      case BorderEndWidth(size: Size)    => borderRightWidth  := (size * sizeMultiplier).toString + "px"

      case BorderTopStyle(line: Line)    => borderTopStyle    := line.toString
      case BorderBottomStyle(line: Line) => borderBottomStyle := line.toString
      case BorderStartStyle(line: Line)  => borderLeftStyle   := line.toString
      case BorderEndStyle(line: Line)    => borderRightStyle  := line.toString

      case BorderRadius(size) => borderRadius := (size * sizeMultiplier).toString + "px"

      case Cursor(cursorVariant) => cursor := cursorVariant.toString

      case FontSize(size) => fontSize := (size * sizeMultiplier * 2).toString + "px"

      case Outline(line) => outline := line.toString

      case DynamicPaddingTop(size) =>
        paddingTop <-- signal.toLaminarSignal(size.map(_ * sizeMultiplier).map(_.toString))
    }
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
