
require File.join(File.dirname(__FILE__), *%w(.. src ruby java-mateview))

class MateExample
  attr_reader :mate_text
  
  def initialize
    @display = Swt::Widgets::Display.new
    @shell = Swt::Widgets::Shell.new(@display)

    build_application_menu
    build_styled_text
    # setup_listeners
    
    @shell.layout = Swt::Layout::FillLayout.new
    @shell.size = Swt::Graphics::Point.new(600, 400)
  end
  
  def run!
    @shell.open
    until @shell.disposed?
      unless @display.read_and_dispatch
        @display.sleep
      end
    end
    @display.dispose
  end
  
  def build_application_menu
    @menu = Swt::Widgets::Menu.new(@shell, Swt::SWT::BAR)
		build_file_menu
		build_edit_menu
		@shell.menu_bar = @menu
  end
  
  def build_file_menu
  	file_header = Swt::Widgets::MenuItem.new(@menu, Swt::SWT::CASCADE)
  	file_header.text = "&File"
	
  	file = Swt::Widgets::Menu.new(@shell, Swt::SWT::DROP_DOWN)
  	file_header.menu = file
	
  	open = Swt::Widgets::MenuItem.new(file, Swt::SWT::PUSH)
  	open.text = "Open"
	
  	close = Swt::Widgets::MenuItem.new(file, Swt::SWT::PUSH)
  	close.text = "Close"
  end
  
  def build_edit_menu
  end
  
  def build_styled_text
		@mate_text = JavaMateView::MateText.new(@shell)
    font = Swt::Graphics::Font.new(@shell.display, "Inconsolata", 15, Swt::SWT::NORMAL)
    @mate_text.get_text_widget.font = font
    # @styled_text.block_selection = true
  end
  
  def setup_listeners
    @mate_text.get_text_widget.add_line_style_listener do |args|
      p [:line_style, args.lineOffset, args.lineText]
    end
  end
end

JavaMateView::Bundle.load_bundles("input/")
p JavaMateView::Bundle.bundles.to_a.map {|b| b.name }
JavaMateView::ThemeManager.load_themes("input/")
p JavaMateView::ThemeManager.themes.to_a.map {|t| t.name }

mate_example = MateExample.new
mate_example.mate_text.set_grammar_by_name("Ruby")
mate_example.run!

