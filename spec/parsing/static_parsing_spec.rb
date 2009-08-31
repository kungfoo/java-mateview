
require 'spec/spec_helper'

describe JavaMateView, "when parsing Ruby from scratch" do
  before(:each) do
    @display = Swt::Widgets::Display.new
    @shell = Swt::Widgets::Shell.new(@display)
    @mt = JavaMateView::MateText.new(@shell)
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
+ source.ruby (0,0)-(1,10) open
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
  
  it "knows content_names of DoublePatterns" do
    @st.text = "def foo(a, b)"
    @mt.parser.root.pretty(0).should == (t=<<END)
+ source.ruby (0,0)-(0,13) open
  + meta.function.method.with-arguments.ruby variable.parameter.function.ruby (0,0)-(0,13) closed
    c keyword.control.def.ruby (0,0)-(0,3) closed
    c entity.name.function.ruby (0,4)-(0,7) closed
    c punctuation.definition.parameters.ruby (0,7)-(0,8) closed
    + punctuation.separator.object.ruby (0,9)-(0,10) closed
    c punctuation.definition.parameters.ruby (0,12)-(0,13) closed
END
  end
  
  it "creates scopes as children of DoublePatterns" do
    @st.text = "\"laura\\nroslin\""
    @mt.parser.root.pretty(0).should == (t=<<END)
+ source.ruby (0,0)-(0,15) open
  + string.quoted.double.ruby (0,0)-(0,15) closed
    c punctuation.definition.string.begin.ruby (0,0)-(0,1) closed
    + constant.character.escape.ruby (0,6)-(0,8) closed
    c punctuation.definition.string.end.ruby (0,14)-(0,15) closed
END
  end
  
  it "creates closing regexes correctly" do
    @st.text = "foo=\<\<END\nstring\nEND"
    @mt.parser.root.pretty(0).should == (t=<<END)
+ source.ruby (0,0)-(2,3) open
  + string.unquoted.heredoc.ruby (0,3)-(2,3) closed
    c punctuation.definition.string.begin.ruby (0,3)-(0,9) closed
    c punctuation.definition.string.end.ruby (2,0)-(2,3) closed
END
  end

  it "creates multiple levels of scopes" do
    @st.text = "\"william \#{:joseph} adama\""
    @mt.parser.root.pretty(0).should == (t=<<END)
+ source.ruby (0,0)-(0,26) open
  + string.quoted.double.ruby (0,0)-(0,26) closed
    c punctuation.definition.string.begin.ruby (0,0)-(0,1) closed
    + source.ruby.embedded.source (0,9)-(0,19) closed
      c punctuation.section.embedded.ruby (0,9)-(0,11) closed
      + constant.other.symbol.ruby (0,11)-(0,18) closed
        c punctuation.definition.constant.ruby (0,11)-(0,12) closed
      c punctuation.section.embedded.ruby (0,18)-(0,19) closed
    c punctuation.definition.string.end.ruby (0,25)-(0,26) closed
END
  end
  
  it "parses some Ruby correctly" do
    @st.text = <<END
class Red < Car
  attr :foo
  Dir["*"].each do |fn|
    p fn
  end
end
END
    @mt.parser.root.pretty(0).should == (t=<<END)
+ source.ruby (0,0)-(6,0) open
  + meta.class.ruby (0,0)-(0,15) closed
    c keyword.control.class.ruby (0,0)-(0,5) closed
    c entity.name.type.class.ruby (0,6)-(0,15) closed
      c entity.other.inherited-class.ruby (0,9)-(0,15) closed
        c punctuation.separator.inheritance.ruby (0,10)-(0,11) closed
  + keyword.other.special-method.ruby (1,2)-(1,6) closed
  + constant.other.symbol.ruby (1,7)-(1,11) closed
    c punctuation.definition.constant.ruby (1,7)-(1,8) closed
  + support.class.ruby (2,2)-(2,5) closed
  + punctuation.section.array.ruby (2,5)-(2,6) closed
  + string.quoted.double.ruby (2,6)-(2,9) closed
    c punctuation.definition.string.begin.ruby (2,6)-(2,7) closed
    c punctuation.definition.string.end.ruby (2,8)-(2,9) closed
  + punctuation.section.array.ruby (2,9)-(2,10) closed
  + punctuation.separator.method.ruby (2,10)-(2,11) closed
  + keyword.control.start-block.ruby (2,16)-(2,19) closed
  + [noname] (2,19)-(2,23) closed
    c punctuation.separator.variable.ruby (2,19)-(2,20) closed
    + variable.other.block.ruby (2,20)-(2,22) closed
    c punctuation.separator.variable.ruby (2,22)-(2,23) closed
  + keyword.control.ruby (4,2)-(4,5) closed
  + keyword.control.ruby (5,0)-(5,3) closed
END
  end

end




