require File.join(File.dirname(__FILE__), "..", "spec_helper.rb")

describe Plist::Dict do
  before(:each) do
    @dict = Plist::Dict.parseFile("spec/fixtures/Ruby.plist")
  end
  
  it "should parse a simple string item" do
    @dict.getString("firstLineMatch").should == "^#!/.*\\bruby\\b"
  end
  
  it "should parse an array of strings" do
    @dict.getStrings("fileTypes").to_a.should ==  %w(rb rbx rjs Rakefile rake cgi fcgi gemspec irbrc capfile)
  end
end