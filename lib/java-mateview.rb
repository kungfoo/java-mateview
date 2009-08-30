require 'java'

$CLASSPATH << File.expand_path(File.join(File.dirname(__FILE__), "..", "bin"))
$:.push(File.expand_path(File.join(File.dirname(__FILE__), "..", "lib")))

require 'jdom'
# TODO: write a method t do this os dependent
require 'rbconfig'
if Config::CONFIG["host_os"] =~ /darwin/
  require 'swt-linux'
else
  require 'swt-linux'
end
require 'swt_wrapper'

unless defined?(JavaMateView)

  class JavaMateView
    import com.redcareditor.mate.MateText
    import com.redcareditor.mate.Grammar
    import com.redcareditor.mate.Bundle
    import com.redcareditor.mate.TextLocation
    import com.redcareditor.theme.Theme
  end

  class Plist
    import com.redcareditor.plist.Dict
    import com.redcareditor.plist.PlistNode
    import com.redcareditor.plist.PlistPropertyLoader
  end

  class Onig
    import com.redcareditor.onig.Rx
    import com.redcareditor.onig.Match
  end
end

class MateExample
  def initialize
    display = Swt::Widgets::Display.new
    @shell = Swt::Widgets::Shell.new(display)

    build_application_menu
    build_styled_text
    setup_listeners
    
    @shell.layout = Swt::Layout::FillLayout.new
    @shell.size = Swt::Graphics::Point.new(600, 400)
    
    @shell.open
    until @shell.disposed?
      unless display.read_and_dispatch
        display.sleep
      end
    end
    display.dispose
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
		@styled_text = JavaMateView::MateText.new(@shell, Swt::SWT::FULL_SELECTION | Swt::SWT::VERTICAL | Swt::SWT::HORIZONTAL)
		font = Swt::Graphics::Font.new(@shell.display, "Inconsolata", 13, Swt::SWT::NORMAL)
		@styled_text.font = font
		@styled_text.block_selection = true
  end
  
  def setup_listeners
    @styled_text.add_verify_listener do |args|
      p [:modified, args, args.start, args.end, args.text]
    end
    
    @styled_text.add_line_style_listener do |args|
      p [:line_style, args.lineOffset, args.lineText]
    end
  end
end

MateExample.new


