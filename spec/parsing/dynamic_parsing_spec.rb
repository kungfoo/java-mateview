
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
    lines.map{|line| line[(whitespace.length)..-1]}.join("\n")
  end
  
  describe "when parsing Ruby from scratch" do
    before(:each) do
      @mt.set_grammar_by_name("Ruby")
    end
    
    it "reparses lines with only whitespace changes" do
      @st.text = strip(<<-END)
      class Red < Car
        def foo
        end
      end
      END
      puts "((()))"
      1.times { @mt.type(1, 9, " ") }
      puts "&&&&&*"
      t1 = @mt.parser.root.pretty(0)
      t2 = @mt.clean_reparse
      puts "**** text"
      p @st.text
      t1.should == t2
    end
  end
end

