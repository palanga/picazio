package picazio

import com.raquo.laminar.api.L.{ Signal as LaminarSignal, * }
import com.raquo.laminar.keys.DerivedStyleProp
import com.raquo.laminar.modifiers.KeySetter.StyleSetter
import com.raquo.laminar.nodes.*
import com.raquo.laminar.nodes.ReactiveHtmlElement.Base
import org.scalajs.dom.Element
import picazio.Shape.*
import picazio.signal.toLaminarSignal
import picazio.style.*
import picazio.style.Style.*
import picazio.theme.*
import zio.*

import scala.util.chaining.scalaUtilChainingOps

private[picazio] class StyleInterpreter(implicit runtime: Runtime[Theme], unsafe: Unsafe) {

  private val theme: Theme = runtime.environment.get[Theme]

  def applyTypography(shape: Shape[?])(styleModifiers: Seq[Modifier[Base]]): Seq[Modifier[Base]] =
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
      case OnKeyPressed(_, inner)  => applyTypography(inner)(styleModifiers)
      case _                       => styleModifiers
    }

  private[picazio] def applyStyles(shape: Shape[?])(element: ReactiveElement[Element]): ReactiveElement[Element] =
    defaultStylesForShape(shape)
      .pipe(applyTheme)
      .pipe(convertToLaminarStyleSetters(shape))
      .pipe(applyTypography(shape))
      .pipe(amendStyles(element))

  private def defaultStylesForShape(shape: Shape[?]): StyleSheet =
    shape match {
      case _: Text                => TextStyles.default
      case _: StaticText          => TextStyles.default
      case _: TextInput           => InputTextStyles.default
      case _: SubscribedTextInput => InputTextStyles.default
      case _: SignaledTextInput   => InputTextStyles.default
      case _: Shape.Button        => ButtonStyles.default
      case _: StaticArray[?]      => ArrayStyles.default
      case _: SignaledArray[?]    => ArrayStyles.default
      case _: StreamedArray[?]    => ArrayStyles.default
      case _: Grid[?]             => ArrayStyles.default
      case _: Background[?]       => BackgroundStyles.default
      case _: OnClick[?, ?]       => OnClickStyles.default
      case _: OnInput[?, ?]       => InputTextStyles.default
      case _: OnInputFilter[?]    => InputTextStyles.default
      case _: OnKeyPressed[?, ?]  => InputTextStyles.default
      case Styled(styles, _)      => styles
      case _                      => StyleSheet.empty
    }

  private def applyTheme(styles: StyleSheet): ThemedStyles = picazio.theme.ThemedStyles(styles, theme)

  private def convertToLaminarStyleSetters(shape: Shape[?])(themedStyles: ThemedStyles): Seq[Modifier[Base]] = {
    val ThemedStyles(styles, Theme(sizeMultiplier, _, colorPalette)) = themedStyles

    def signalToModifierWithDerivedStyleProp[A, B](
      signal: Signal[A]
    )(f: A => B)(prop: DerivedStyleProp[B]): Seq[Modifier[Base]] = signal match {
      case s: ConstantSignal[?] => Seq(prop := f(s.self))
      case _                    => Seq(prop <-- toLaminarSignal(signal.map(f)))
    }

    def signalToModifierWithStyleProp[A, B](signal: Signal[A])(f: A => B)(prop: StyleProp[B]): Seq[Modifier[Base]] =
      signal match {
        case s: ConstantSignal[?] => Seq(prop := f(s.self))
        case _                    => Seq(prop <-- toLaminarSignal(signal.map(f)))
      }

    def signalToModifier[A](signal: Signal[A])(f: A => Seq[StyleSetter]): Seq[Modifier[Base]] = signal match {
      case s: ConstantSignal[?] => f(s.self)
      case _                    => Seq.empty // TODO
    }

    def sizeToProp(size: Size): String = size match {
      case Size.Units(self)   => s"${self * sizeMultiplier}px"
      case Size.Percent(self) => s"$self%"
    }

    def styleToLaminarModifierSeq(style: Style): Seq[Modifier[Base]] = style match {

      case MarginTop(size)    => signalToModifierWithStyleProp(size)(sizeToProp)(marginTop)
      case MarginBottom(size) => signalToModifierWithStyleProp(size)(sizeToProp)(marginBottom)
      case MarginStart(size)  => signalToModifierWithStyleProp(size)(sizeToProp)(marginLeft)
      case MarginEnd(size)    => signalToModifierWithStyleProp(size)(sizeToProp)(marginRight)

      case PaddingTop(size)    => signalToModifierWithStyleProp(size)(sizeToProp)(paddingTop)
      case PaddingBottom(size) => signalToModifierWithStyleProp(size)(sizeToProp)(paddingBottom)
      case PaddingStart(size)  => signalToModifierWithStyleProp(size)(sizeToProp)(paddingLeft)
      case PaddingEnd(size)    => signalToModifierWithStyleProp(size)(sizeToProp)(paddingRight)

      case SelfAlignment(alignment) =>
        alignment match {
          case signal: ConstantSignal[?] =>
            shape match {
              case Styled(_, StaticText(_)) =>
                Seq(
                  textAlign    := signal.self.toString,
                  alignContent := signal.self.toString,
                  flexShrink(0),
                )
              case Styled(_, Text(_))       =>
                Seq(
                  textAlign    := signal.self.toString,
                  alignContent := signal.self.toString,
                  flexShrink(0),
                )
              case _                        =>
                Seq(alignSelf := signal.self.toString)
            }
          case signal                    =>
            shape match {
              case Styled(_, StaticText(_)) =>
                Seq(
                  textAlign <-- toLaminarSignal(signal.map(_.toString)),
                  alignContent <-- toLaminarSignal(signal.map(_.toString)),
                  flexShrink(0),
                )
              case Styled(_, Text(_))       =>
                Seq(
                  textAlign <-- toLaminarSignal(signal.map(_.toString)),
                  alignContent <-- toLaminarSignal(signal.map(_.toString)),
                  flexShrink(0),
                )
              case _                        =>
                Seq(alignSelf <-- toLaminarSignal(signal.map(_.toString)))
            }
        }

      case Width(percentage) => signalToModifierWithDerivedStyleProp(percentage)(identity)(width.percent)

      case FixHeight(size) => signalToModifierWithStyleProp(size)(sizeToProp)(height)
      case FixWidth(size)  => signalToModifierWithStyleProp(size)(sizeToProp)(width)

      case JustifyContent(justification) => signalToModifierWithStyleProp(justification)(_.toString)(justifyContent)

      case BorderTopWidth(size)    => signalToModifierWithStyleProp(size)(sizeToProp)(borderTopWidth)
      case BorderBottomWidth(size) => signalToModifierWithStyleProp(size)(sizeToProp)(borderBottomWidth)
      case BorderStartWidth(size)  => signalToModifierWithStyleProp(size)(sizeToProp)(borderLeftWidth)
      case BorderEndWidth(size)    => signalToModifierWithStyleProp(size)(sizeToProp)(borderRightWidth)

      case BorderTopStyle(line)    => signalToModifierWithStyleProp(line)(_.toString)(borderTopStyle)
      case BorderBottomStyle(line) => signalToModifierWithStyleProp(line)(_.toString)(borderBottomStyle)
      case BorderStartStyle(line)  => signalToModifierWithStyleProp(line)(_.toString)(borderLeftStyle)
      case BorderEndStyle(line)    => signalToModifierWithStyleProp(line)(_.toString)(borderRightStyle)

      case BorderRadius(size) => signalToModifierWithStyleProp(size)(sizeToProp)(borderRadius)

      case ColorStyle(color_) =>
        shape match {
          case Styled(_, Background(_)) =>
            signalToModifierWithStyleProp(color_)(colorPalette.get(_).toString)(backgroundColor)
          case _                        => signalToModifierWithStyleProp(color_)(colorPalette.get(_).toString)(color)
        }

      case BackgroundColorStyle(color_) =>
        signalToModifierWithStyleProp(color_)(colorPalette.get(_).toString)(backgroundColor)

      case CursorStyle(cursor_) => signalToModifierWithStyleProp(cursor_)(_.toString)(cursor)

      case FontSize(size) => signalToModifierWithStyleProp(size)(size => sizeToProp(size * 2))(fontSize)

      case Outline(line) => signalToModifierWithStyleProp(line)(_.toString)(outline)

      case Overflowing(overflow_) =>
        signalToModifier(overflow_) {
          case Overflow.Hidden   => Seq(overflow.hidden)
          case Overflow.Ellipsis => Seq(textOverflow.ellipsis, overflow.hidden, whiteSpace.nowrap)
          case Overflow.Scroll   => Seq(overflow.scroll)
        }

      case Wrapping(wrap_) =>
        signalToModifier(wrap_) {
          case Wrap.NoWrap         => Seq(whiteSpace.nowrap)
          case Wrap.WhiteSpaceWrap => Seq(whiteSpace.normal)
        }
    }

    styles.values.flatMap(styleToLaminarModifierSeq)

  }

  private def amendStyles(
    element: ReactiveElement.Base
  )(styleModifiers: Seq[Modifier[Base]]): ReactiveElement.Base =
    element match {
      case html: ReactiveHtmlElement[?] => html.amend(styleModifiers)
      case svg: ReactiveSvgElement[?]   => svg
      case _                            =>
        throw new IllegalArgumentException(
          s"ReactiveHtmlElement or ReactiveSvgElement expected. Got: ${element.getClass.getName}"
        )
    }

}
