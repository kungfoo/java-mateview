require File.join(File.dirname(__FILE__), "..", "spec_helper.rb")

describe JavaMateView::Theme do
  describe "it has global settings" do
    before(:all) do
      dict = Plist::Dict.parseFile("spec/fixtures/Railscasts.tmTheme")
      p dict.getString("name")
      theme = JavaMateView::Theme.new(theme)
    end
    
    it "sets the background" do
      theme.globalSettings.get("background").should == "#2B2B2B"
    end
  end
end