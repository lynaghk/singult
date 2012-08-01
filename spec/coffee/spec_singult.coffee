xhtmlns = "http://www.w3.org/1999/xhtml"
svgns = "http://www.w3.org/2000/svg"



describe "namespace_tag", ->
  it "should default to xhtml namespace", ->
    expect(namespace_tag("div")).toEqual [xhtmlns, "div"]
    expect(namespace_tag("img")).toEqual [xhtmlns, "img"]

  it "should infer svg namespace", ->
    expect(namespace_tag("circle")).toEqual [svgns, "circle"]
    expect(namespace_tag("g")).toEqual [svgns, "g"]

describe "canonicalize", ->
  it "should canonicalize with attr", ->
    plain = ["div", {}]
    expect(canonicalize(plain)).toEqual
      nsp: xhtmlns, tag: "div", children: [], attr: {}

    with_id = ["div#grr", {}]
    expect(canonicalize(with_id)).toEqual
      nsp: xhtmlns, tag: "div", children: [], attr: {id: "grr"}

    with_id_class = ["div#grr.bar", {}]
    expect(canonicalize(with_id_class)).toEqual
      nsp: xhtmlns, tag: "div", children: [], attr: {id: "grr", class: "bar"}

  it "should canonicalize without attr", ->
    expect(canonicalize ["div"]).toEqual
      nsp: xhtmlns, tag: "div", children: [], attr: {}

  it "should canonicalize strings and numbers to strings", ->
    expect(canonicalize "str").toEqual "str"
    expect(canonicalize 5).toEqual "5"

  it "should explode children as appropriate", ->
    expect(canonicalize ["div", [":*:", 1, 2, 3]]).toEqual
      nsp: xhtmlns, tag: "div", children: ["1", "2", "3"], attr: {}

