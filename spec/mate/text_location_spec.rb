require File.join(File.dirname(__FILE__), "..", "spec_helper.rb")

describe JavaMateView::TextLocation do
  def newtl(x,y); JavaMateView::TextLocation.new(x,y); end
  
  it "converts to a pretty string correctly" do
    tl = JavaMateView::TextLocation.new(10, 15)
    tl.to_string.should == "{10,15}"
  end
  
  describe "equals" do
    it "returns true for equal textlocations" do
      newtl(10, 15).equals(newtl(10, 15)).should be_true
    end

    it "returns false for different textlocations" do
      newtl(10, 15).equals(newtl(11, 15)).should be_false
      newtl(10, 15).equals(newtl(10, 25)).should be_false
      newtl(1, 5).equals(newtl(10, 25)).should be_false
    end
  end
  
  describe "compareTo" do
    it "returns -1 if the receiver is before the argument" do
      newtl(10, 0).compareTo(newtl(100,0)).should == -1
    end
    
    it "returns 0 if the receiver is at the same place as argument" do
      newtl(10, 0).compareTo(newtl(10,0)).should == 0
    end
    
    it "returns 1 if the receiver is after to the argument" do
      newtl(100, 0).compareTo(newtl(10,0)).should == 1
    end
  end
end