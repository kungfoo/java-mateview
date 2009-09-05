
require File.join(File.dirname(__FILE__), *%w(.. src ruby java-mateview))


JavaMateView::Bundle.load_bundles("input/")
JavaMateView::ThemeManager.load_themes("input/")

class JavaMateView::MateText
  
  def type(line, line_offset, char)
    line_start = get_text_widget.get_offset_at_line(line)
    getDocument.replace(line_start + line_offset, 3, char)
  end
    # 
    # def backspace(line, pos)
    #   delete(get_iter_at_line_offset(line, pos-1),
    #          get_iter_at_line_offset(line, pos))
    # end
    # 
  def clean_reparse
    shell = Swt::Widgets::Shell.new($display)
    mt = JavaMateView::MateText.new(shell)
    mt.set_grammar_by_name(self.parser.grammar.name)
    st = mt.get_text_widget
    st.text = get_text_widget.getText
    mt.parser.root.pretty(0)
  end
  
  def pretty
    parser.root.pretty(0)
  end
end