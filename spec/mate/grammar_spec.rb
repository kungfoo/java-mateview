require File.join(File.dirname(__FILE__), "..", "spec_helper.rb")

describe JavaMateView::Grammar do
  before(:all) do
    @plist = Plist::Dict.parseFile("spec/fixtures/Ruby.plist")
  end
      
  before(:each) do
    @grammar = JavaMateView::Grammar.new(@plist)
  end
  
  describe "initForReference" do 
    before(:each) do
      @grammar.init_for_reference
    end
    
    it "sets the name" do
      @grammar.name.should == "Ruby"
    end
    
    it "sets the scopeName" do
      @grammar.scopeName.should == "source.ruby"
    end
    
    it "sets the keyEquivalent" do
      @grammar.keyEquivalent.should == "^~R"
    end
    
    it "sets the firstLineMatch" do
      @grammar.firstLineMatch.toString.should == "^#!/.*\\bruby\\b"
    end

    it "sets the fileTypes" do
      @grammar.fileTypes.to_a.should == %w(rb rbx rjs Rakefile rake cgi fcgi gemspec irbrc capfile)
    end
  end
  
  describe "initForUse" do
    it "runs" do
      @grammar.initForUse
    end
    
  end
end
