package picazio

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.*
import com.raquo.laminar.nodes.ReactiveHtmlElement.Base
import org.scalajs.dom.Element
import picazio.Shape.*
import picazio.style.*
import picazio.style.Style.*
import picazio.theme.*
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
      .pipe(convertToLaminarStyleSetters(shape))
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
      case _: StaticColumn         => SequenceStyles.default
      case _: DynamicColumn        => SequenceStyles.default
      case _: StreamColumn         => SequenceStyles.default
      case _: StaticRow            => SequenceStyles.default
      case _: DynamicRow           => SequenceStyles.default
      case _: Background           => BackgroundStyles.default
      case Styled(styles, inner)   => defaultStylesForShape(inner) ++ styles
      case OnClick(_, inner)       => defaultStylesForShape(inner) ++ OnClickStyles.default
      case OnInput(_, inner)       => defaultStylesForShape(inner)
      case OnInputFilter(_, inner) => defaultStylesForShape(inner)
      case _                       => StyleSheet.empty
    }

  private def applyTheme(styles: StyleSheet): ThemedStyles = picazio.theme.ThemedStyles(styles, theme)

  private def convertToLaminarStyleSetters(shape: Shape)(themedStyles: ThemedStyles): Seq[Modifier[Base]] = {
    val ThemedStyles(styles, Theme(sizeMultiplier, _, colorPalette)) = themedStyles
    styles.values.flatMap {
      case MarginTop(size)    => Seq(marginTop := (size * sizeMultiplier).toString + "px")
      case MarginBottom(size) => Seq(marginBottom := (size * sizeMultiplier).toString + "px")
      case MarginStart(size)  => Seq(marginLeft := (size * sizeMultiplier).toString + "px")
      case MarginEnd(size)    => Seq(marginRight := (size * sizeMultiplier).toString + "px")

      case PaddingTop(size)    => Seq(paddingTop := (size * sizeMultiplier).toString + "px")
      case PaddingBottom(size) => Seq(paddingBottom := (size * sizeMultiplier).toString + "px")
      case PaddingStart(size)  => Seq(paddingLeft := (size * sizeMultiplier).toString + "px")
      case PaddingEnd(size)    => Seq(paddingRight := (size * sizeMultiplier).toString + "px")

      case SelfAlignment(alignment) => Seq(alignSelf := alignment.toString)

      case Width(percentage) => Seq(width.percent(percentage))

      case JustifyContent(justification) => Seq(justifyContent := justification.toString)

      case BorderTopWidth(size: Size)    => Seq(borderTopWidth := (size * sizeMultiplier).toString + "px")
      case BorderBottomWidth(size: Size) => Seq(borderBottomWidth := (size * sizeMultiplier).toString + "px")
      case BorderStartWidth(size: Size)  => Seq(borderLeftWidth := (size * sizeMultiplier).toString + "px")
      case BorderEndWidth(size: Size)    => Seq(borderRightWidth := (size * sizeMultiplier).toString + "px")

      case BorderTopStyle(line: Line)    => Seq(borderTopStyle := line.toString)
      case BorderBottomStyle(line: Line) => Seq(borderBottomStyle := line.toString)
      case BorderStartStyle(line: Line)  => Seq(borderLeftStyle := line.toString)
      case BorderEndStyle(line: Line)    => Seq(borderRightStyle := line.toString)

      case BorderRadius(size) => Seq(borderRadius := (size * sizeMultiplier).toString + "px")

      case ColorStyle(color_) =>
        shape match {
          case Styled(_, Background(_)) => Seq(backgroundColor := colorPalette.get(color_).toString)
          case _                     => Seq(color := colorPalette.get(color_).toString)
        }

      case BackgroundColorStyle(color_) => Seq(backgroundColor := colorPalette.get(color_).toString)

      case CursorStyle(cursor_) => Seq(cursor := cursor_.toString)

      case FontSize(size) => Seq(fontSize := (size * sizeMultiplier * 2).toString + "px")

      case Outline(line) => Seq(outline := line.toString)

      case Overflowing(overflow_) =>
        overflow_ match {
          case Overflow.Hidden   => Seq(overflow.hidden)
          case Overflow.Ellipsis => Seq(textOverflow.ellipsis, overflow.hidden, whiteSpace.nowrap)
          case Overflow.Scroll   => Seq(overflow.scroll)
        }

      case Wrapping(wrap_) =>
        wrap_ match {
          case Wrap.NoWrap         => Seq(whiteSpace.nowrap)
          case Wrap.WhiteSpaceWrap => Seq(whiteSpace.normal)
        }

      case DynamicPaddingTop(size) =>
        Seq(paddingTop <-- signal.toLaminarSignal(size.map(_ * sizeMultiplier).map(_.toString)))
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
