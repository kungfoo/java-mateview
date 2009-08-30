
require 'spec/spec_helper'

describe JavaMateView, "when parsing Ruby from scratch" do
  before(:each) do
    @display = Swt::Widgets::Display.new
    @shell = Swt::Widgets::Shell.new(@display)
    @mt = JavaMateView::MateText.new(
		  JavaMateView::MateText.constructContents(@shell), 
		  ruler = JavaMateView::MateText.constructRuler, 
		  Swt::SWT::FULL_SELECTION | Swt::SWT::VERTICAL | Swt::SWT::HORIZONTAL)
    @mt.set_grammar_by_name("Ruby")
  end
  
  after(:each) do
    @mt.get_text_widget.dispose
    @shell.dispose
    @display.dispose
  end
  
  it "does something" do
    @mt.get_text_widget.get_line_count.should == 1
  end
  
  it "should have a blank Ruby scope tree" do
    @mt.parser.root.pretty(0).should == (t=<<END)
+ source.ruby () open
END
  end
  
  it "parses flat SinglePatterns" do
    @mt.get_text_widget.text = "1 + 2 + Redcar"
    @mt.parser.root.pretty(0).should == (t=<<END)
+ source.ruby (0,0)-(0,14) open
  + constant.numeric.ruby (0,0)-(0,1) closed
  + keyword.operator.arithmetic.ruby (0,2)-(0,3) closed
  + constant.numeric.ruby (0,4)-(0,5) closed
  + keyword.operator.arithmetic.ruby (0,6)-(0,7) closed
  + variable.other.constant.ruby (0,8)-(0,14) closed
END
  end
end