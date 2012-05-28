#Test runner for use with PhantomJS
page = new WebPage()
page.onConsoleMessage = (msg) -> console.log msg

page.onLoadFinished = ->
  page.injectJs "public/cljs_test.js"
  phantom.exit()

page.open "about:blank"
