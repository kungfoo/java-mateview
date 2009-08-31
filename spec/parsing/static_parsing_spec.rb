
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
    @st = @mt.get_text_widget
  end
  
  after(:each) do
    @mt.get_text_widget.dispose
    @shell.dispose
    @display.dispose
  end
  
  it "does something" do
    @st.get_line_count.should == 1
  end
  
  it "should have a blank Ruby scope tree" do
    @mt.parser.root.pretty(0).should == (t=<<END)
+ source.ruby (0,0)-(0,0) open
END
  end
  
  it "parses flat SinglePatterns" do
    @st.text = "1 + 2 + Redcar"
    @mt.parser.root.pretty(0).should == (t=<<END)
+ source.ruby (0,0)-(0,14) open
  + constant.numeric.ruby (0,0)-(0,1) closed
  + keyword.operator.arithmetic.ruby (0,2)-(0,3) closed
  + constant.numeric.ruby (0,4)-(0,5) closed
  + keyword.operator.arithmetic.ruby (0,6)-(0,7) closed
  + variable.other.constant.ruby (0,8)-(0,14) closed
END
  end
  
  it "parses flat SinglePatterns on multiple lines" do
    @st.text = "1 + \n3 + Redcar"
    @mt.parser.root.pretty(0).should == (t=<<END)
+ source.ruby (0,0)-(1,15) open
  + constant.numeric.ruby (0,0)-(0,1) closed
  + keyword.operator.arithmetic.ruby (0,2)-(0,3) closed
  + constant.numeric.ruby (1,0)-(1,1) closed
  + keyword.operator.arithmetic.ruby (1,2)-(1,3) closed
  + variable.other.constant.ruby (1,4)-(1,10) closed
END
  end
  
  it "arranges SinglePattern captures into trees" do
    @st.text = "class Red < Car"
    @mt.parser.root.pretty(0).should == (t=<<END)
+ source.ruby (0,0)-(0,15) open
  + meta.class.ruby (0,0)-(0,15) closed
    c keyword.control.class.ruby (0,0)-(0,5) closed
    c entity.name.type.class.ruby (0,6)-(0,15) closed
      c entity.other.inherited-class.ruby (0,9)-(0,15) closed
        c punctuation.separator.inheritance.ruby (0,10)-(0,11) closed
END
  end
  
  it "opens DoublePatterns" do
    @st.text = "\"asdf"
    @mt.parser.root.pretty(0).should == (t=<<END)
+ source.ruby (0,0)-(0,5) open
  + string.quoted.double.ruby (0,0)-(0,5) open
    c punctuation.definition.string.begin.ruby (0,0)-(0,1) closed
END
  end
    
  it "closes DoublePatterns" do
    @st.text = "\"asdf\""
    @mt.parser.root.pretty(0).should == (t=<<END)
+ source.ruby (0,0)-(0,6) open
  + string.quoted.double.ruby (0,0)-(0,6) closed
    c punctuation.definition.string.begin.ruby (0,0)-(0,1) closed
    c punctuation.definition.string.end.ruby (0,5)-(0,6) closed
END
  end

end