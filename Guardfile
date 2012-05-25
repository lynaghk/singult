guard 'coffeescript', :input => 'src/coffee', :output => 'dev_public/js', :bare => true
guard 'coffeescript', :input => 'spec/coffee', :output => 'spec/javascripts', :bare => true

guard 'haml', :input => 'src/haml', :output => 'public' do
  watch %r{^src/haml/.+\.haml$}
end

guard :jasmine, :jasmine_url => 'http://localhost:8888/' do
  watch(%r{^dev_public/js/.+\.js$})
  watch(%r{^spec/javascripts/.+\.js$})
end

guard 'livereload', :apply_js_live => false do
  watch(%r{public/.+\.(css|js|html)})
  watch(%r{^spec/javascripts/.+\.js$})
end
