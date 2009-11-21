
require File.join(File.dirname(__FILE__), *%w(.. src ruby java-mateview))

class MateExample < Jface::ApplicationWindow
  attr_reader :mate_text, :contents
  
  def initialize
    super(nil)
  end
  
  def createContents(parent)
    @contents = Swt::Widgets::Composite.new(parent, Swt::SWT::NONE)
    @contents.layout = Swt::Layout::FillLayout.new
    @mate_text = JavaMateView::MateText.new(@contents)
    @mate_text.set_grammar_by_name "Ruby"
    @mate_text.set_theme_by_name "Mac Classic"
    @mate_text.set_font "Monaco", 15
    return @contents
  end
  
  def initializeBounds
    shell.set_size(500,400)
  end
  
  def createMenuManager
    main_menu = Jface::MenuManager.new
    
    file_menu = Jface::MenuManager.new("Tests")
    main_menu.add file_menu
    
    replace1_action = ReplaceContents1.new
    replace1_action.window = self
    replace1_action.text = "Contents RUBY"
    file_menu.add replace1_action
    
    replace2_action = ReplaceContents2.new
    replace2_action.window = self
    replace2_action.text = "Contents HTML"
    file_menu.add replace2_action
    
    set_ruby_action = SetRuby.new
    set_ruby_action.window = self
    set_ruby_action.text = "Set Ruby Grammar"
    file_menu.add set_ruby_action
    
    set_html_action = SetHTML.new
    set_html_action.window = self
    set_html_action.text = "Set HTML Grammar"
    file_menu.add set_html_action
    
    set_mc_action = SetMacClassic.new
    set_mc_action.window = self
    set_mc_action.text = "Set Mac Classic"
    file_menu.add set_mc_action
    
    set_twilight_action = SetTwilight.new
    set_twilight_action.window = self
    set_twilight_action.text = "Set Twilight"
    file_menu.add set_twilight_action
    return main_menu
  end
  
  class SetMacClassic < Jface::Action
    attr_accessor :window
    
    def run
      @window.mate_text.set_theme_by_name("Mac Classic")
    end
  end
  
  class SetTwilight < Jface::Action
    attr_accessor :window
    
    def run
      @window.mate_text.set_theme_by_name("Twilight")
    end
  end
  
  class SetRuby < Jface::Action
    attr_accessor :window
    
    def run
      @window.mate_text.set_grammar_by_name("Ruby")
    end
  end
  
  class SetHTML < Jface::Action
    attr_accessor :window
    
    def run
      @window.mate_text.set_grammar_by_name("HTML")
    end
  end
  
  class ReplaceContents1 < Jface::Action
    attr_accessor :window

    def run
      @window.mate_text.getMateDocument.set(source*3)
    end
    
    def source
      foo=<<-RUBY
class ExitAction < Jface::Action
  attr_accessor :window

  def run
    window.close
  end
end
      
RUBY
    end
  end
  
  class ReplaceContents2 < Jface::Action
    attr_accessor :window

    def run
      @window.mate_text.getMateDocument.set(source*3)
    end
    
    def source
      foo=<<-HTML
<div class="nav">
  <ul>
    <li>Foo</li>
    <li>Bar</li>
    <li>Baz</li>
  </ul>
</div>
      
HTML
    end
  end
  
  def self.run
    JavaMateView::Bundle.load_bundles("input/")
    p JavaMateView::Bundle.bundles.to_a.map {|b| b.name }
    JavaMateView::ThemeManager.load_themes("input/")
    p JavaMateView::ThemeManager.themes.to_a.map {|t| t.name }
    
    window = MateExample.new
    window.block_on_open = true
    window.addMenuBar
    window.open
    Swt::Widgets::Display.getCurrent.dispose
  end
end

MateExample.run

