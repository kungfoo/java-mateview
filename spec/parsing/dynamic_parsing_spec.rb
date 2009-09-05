
require 'spec/spec_helper'

describe JavaMateView, "when reparsing after changes" do
  before(:each) do
    $display ||= Swt::Widgets::Display.new
    @shell = Swt::Widgets::Shell.new($display)
    @mt = JavaMateView::MateText.new(@shell)
    @st = @mt.get_text_widget
  end
  
  after(:each) do
    @mt.get_text_widget.dispose
    @shell.dispose
  end
  
  def strip(text)
    lines = text.split("\n")
    lines.first =~ /^(\s*)/
    whitespace = $1 || ""
    result = lines.map{|line| line[(whitespace.length)..-1]}.join("\n")
    p result
    p result.length
    result
  end
  
  def it_should_match_clean_reparse
    @mt.parser.root.pretty(0).should == @mt.clean_reparse
  end
  
  def it_should_match_clean_reparse_debug
    @mt.parser.root.pretty(0).should == @mt.clean_reparse
  end
  
  describe "when parsing Ruby" do
    before(:each) do
      @mt.set_grammar_by_name("Ruby")
    end
  #   
    it "reparses lines with only whitespace changes" do
      @st.text = strip(<<-END)
      class Red < Car
        def foo
        end
      end
      END
      1.times { @mt.type(1, 9, " ") }
      it_should_match_clean_reparse
    end
    
    it "reparses lines with only whitespace changes, even when they have scope openers" do
      @st.text = strip(<<-END)
      puts "hello"
      foo=<<HI
        Here.foo
        Here.foo
      HI
      puts "hello"
      END
      5.times { @mt.type(1, 8, " ") }
      it_should_match_clean_reparse
    end
    
    it "reparses flat SinglePatterns that have no changes to scopes" do
      @st.text = "1 + 2 + Redcar"
      puts "((((()))))"
      @mt.type(0, 1, " ")
      puts "**********"
      it_should_match_clean_reparse
    end
            
    it "reparses flat SinglePatterns that have changes to scopes" do
      @st.text = "1 + 2 + Redcar"
      @mt.type(0, 4, "2")
      @mt.type(0, 12, "o")
      it_should_match_clean_reparse
    end
    
    it "reparses when blank lines inserted" do
      @st.text = strip(<<-END)
      class Red < Car
        def foo
        end
      end
      END
      @mt.type(1, 0, "\n")
      @mt.type(1, 0, "\n")
      it_should_match_clean_reparse
    end

    it "reparses lines with only whitespace changes, even when they have closing scopes" do
      @st.text = strip(<<-END)
      puts "hello"
      foo=<<HI
        Here.foo
        Here.foo
      HI
      puts "hello"
      END
      puts "(((((())))))"
      1.times { @mt.type(4, 2, " ") }
      puts "***********"
      it_should_match_clean_reparse
    end

  end
end

