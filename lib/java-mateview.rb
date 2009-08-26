require 'java'

$CLASSPATH << File.expand_path(File.join(File.dirname(__FILE__), "..", "bin"))
$:.push(File.expand_path(File.join(File.dirname(__FILE__), "..", "lib")))

require 'jdom'
require 'swt'
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

    @shell.pack
    @shell.open
    until @shell.disposed?
      unless display.readAndDispatch
        display.sleep
      end
    end
    display.dispose
  end
  
  def build_application_menu
    @menu = Swt::Widgets::Menu.new(@shell, Swt::SWT::BAR)
		build_file_menu
		build_edit_menu
		@shell.set_menu_bar(@menu)
  end
  
  def build_file_menu
  	file_header = Swt::Widgets::MenuItem.new(@menu, Swt::SWT::CASCADE)
  	file_header.set_text("&File")
	
  	file = Swt::Widgets::Menu.new(@shell, Swt::SWT::DROP_DOWN)
  	file_header.set_menu(file)
	
  	open = Swt::Widgets::MenuItem.new(file, Swt::SWT::PUSH)
  	open.set_text("Open")
	
  	close = Swt::Widgets::MenuItem.new(file, Swt::SWT::PUSH)
  	close.set_text("Close")
  end
  
  def build_styled_text
		@styled_text = JavaMateView::MateText.new(@shell, Swt::SWT::FULL_SELECTION | Swt::SWT::VERTICAL | Swt::SWT::HORIZONTAL)
		font = Swt::Graphics::Font.new(@shell.get_display, "Monaco", 16, Swt::SWT::NORMAL)
		@styled_text.set_font(font)
		@styled_text.set_size(400, 300)
  end
  
  def setup_listeners
    @styled_text.add_verify_listener do |args|
      p [:modified, args, args.start, args.end, args.text]
    end
  end
  
  def build_edit_menu
  end
end


MateExample.new


