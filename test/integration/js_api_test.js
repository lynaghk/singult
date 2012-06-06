//Very limited testing of JS API.
//Most Singult tests are through ClojureScript; see test.cljs



function p(x) {
  console.log(x);
  return x;
}

function AssertException(message) { this.message = message; }
AssertException.prototype.toString = function () {
  return 'AssertException: ' + this.message;
};

function assert(msg, exp) {
  if (!exp) {
    throw new AssertException(msg);
  }
}


$test = document.querySelector("#test");

a = ["div", {a: 1}, "hello"];
$a = singult.render(a);
$test.appendChild($a);

assert("Text renders", document.body.innerText == "hello");


a[2] = "new text";
singult.merge($a, a);
assert("Child text merges", document.body.innerText == "new text");


alert("Hurray, all JS API tests passed!");
