
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
    @mate_text.set_theme_by_name "Railscasts"
    return @contents
  end
  
  def initializeBounds
    shell.set_size(500,400)
  end
  
  def createMenuManager
    main_menu = Jface::MenuManager.new
    
    file_menu = Jface::MenuManager.new("File")
    main_menu.add file_menu
    #file_menu.add exit_action
    return main_menu
  end
  
  class ExitAction < Jface::Action
    attr_accessor :window
    
    def initialize(window)
      @window = window
      text = "Exit@Cmd+Q"
    end
    
    def run
      window.close
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

