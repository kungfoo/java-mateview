require 'rake/clean'

task :default => 'jruby:test'

namespace :java do
  desc "Rebuild the java class files"
  task :compile do
    sh %+ant compile+
  end
  
  desc "Run jUnit tests against freshly compiled java classes"
  task :test => [:compile] do
    sh %+ant test+
  end
end

namespace :jruby do
  desc "Run ruby tests against a freshly compiled build"
  task :test => ['java:test'] do
    sh %+/Applications/cli/jruby-1.3.1/bin/spec spec/+
  end
end